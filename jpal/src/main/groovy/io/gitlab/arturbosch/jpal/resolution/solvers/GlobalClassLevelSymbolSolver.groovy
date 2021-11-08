package io.gitlab.arturbosch.jpal.resolution.solvers

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName
import com.github.javaparser.ast.type.ClassOrInterfaceType
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.symbols.FieldSymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.MethodSymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.SymbolReference

/**
 * @author Artur Bosch
 */
@CompileStatic
final class GlobalClassLevelSymbolSolver extends CallOrAccessAwareSolver implements Solver {

	private TypeSolver resolver
	private CompilationStorage storage
	private LocalClassLevelSymbolSolver classLevelSolver
	private MethodLevelVariableSymbolSolver methodLevelSolver
	private ObjectCreationSymbolSolver objectCreationSolver
	private NameLevelSymbolSolver nameLevelSolver

	GlobalClassLevelSymbolSolver(CompilationStorage storage, TypeSolver resolver,
								 NameLevelSymbolSolver nameLevelSolver,
								 LocalClassLevelSymbolSolver classLevelSolver) {
		this.storage = storage
		this.resolver = resolver
		this.classLevelSolver = classLevelSolver
		this.methodLevelSolver = new MethodLevelVariableSymbolSolver(resolver)
		this.objectCreationSolver = new ObjectCreationSymbolSolver(resolver)
		this.nameLevelSolver = nameLevelSolver
	}

	@Override
	Optional<? extends SymbolReference> resolve(SimpleName symbol, CompilationInfo info) {
		// MethodCalls and FieldAccesses must be handled before searching locally for declarations
		// for this we use the local class level resolver as he will check for 'this' or no scope
		// (eg. this.call() or call() or this.field or field)
		def symbolReference = classLevelSolver.resolve(symbol, info)
		if (symbolReference.isPresent()) return symbolReference

		// Ok, symbol does not belong to a local class, let's search global

		symbolReference = withNotNull(asFieldAccess(symbol)) {
			resolveFieldSymbolGlobal(symbol, it, info)
		}
		if (symbolReference.isPresent()) return symbolReference

		symbolReference = withNotNull(asMethodCall(symbol)) {
			resolveMethodSymbolGlobal(symbol, it, info)
		}
		if (symbolReference.isPresent()) return symbolReference

		return Optional.empty()
	}

	Optional<FieldSymbolReference> resolveFieldSymbolGlobal(SimpleName symbol,
															FieldAccessExpr maybeFieldAccess,
															CompilationInfo info) {
		// is access not chained? then it must belong to a name expr
		SymbolReference symbolReference = nameLevelSolver.resolveNameExprScope(Optional.of(maybeFieldAccess.scope) as Optional<Node>, info)
		if (symbolReference) {
			return resolveFieldInTypeScope(symbolReference.qualifiedType, symbol)
		}

		// not chained but within an object creation expression?
		SymbolReference objReference = objectCreationSolver.resolveInFieldAccess(symbol, maybeFieldAccess, info).orElse(null)
		if (objReference) {
			return resolveFieldInTypeScope(objReference.qualifiedType, symbol)
		}

		// loop through field/method accesses
		symbolReference = asMethodOrFieldChain(Optional.of(maybeFieldAccess.scope), info)
		if (symbolReference) {
			def fieldReference = resolveFieldInTypeScope(symbolReference.qualifiedType, symbol)
			fieldReference.ifPresent {
				def reference = it as FieldSymbolReference
				reference.previousReference = symbolReference
			}
			return fieldReference
		}

		return Optional.empty()
	}

	Optional<MethodSymbolReference> resolveMethodSymbolGlobal(SimpleName symbol,
															  MethodCallExpr maybeCallExpr,
															  CompilationInfo info) {
		// not chained but in scope of a name expr? -> we resolve the class and search for this symbol in methods
		SymbolReference symbolReference = nameLevelSolver.resolveNameExprScope(maybeCallExpr.scope as Optional<Node>, info)
		if (symbolReference) {
			return resolveMethodInTypeScope(symbolReference.qualifiedType, symbol, maybeCallExpr)
		}

		// not chained but within an object creation expression?
		symbolReference = objectCreationSolver.resolveInMethodCall(symbol, maybeCallExpr, info).orElse(null)
		if (symbolReference) {
			return resolveMethodInTypeScope(symbolReference.qualifiedType, symbol, maybeCallExpr)
		}

		// loop through field/method accesses
		symbolReference = asMethodOrFieldChain(maybeCallExpr.scope, info)
		if (symbolReference) {
			def methodReference = resolveMethodInTypeScope(symbolReference.qualifiedType, symbol, maybeCallExpr)
			methodReference.ifPresent {
				def reference = it as MethodSymbolReference
				reference.previousReference = symbolReference
			}
			return methodReference
		}

		return Optional.empty()
	}

	private SymbolReference asMethodOrFieldChain(Optional<Expression> scope, CompilationInfo info) {
		scope.filter { it instanceof FieldAccessExpr || it instanceof MethodCallExpr }
				.filter { it instanceof NodeWithSimpleName }
				.map { it as NodeWithSimpleName }
				.map { resolve(it.name, info).orElse(null) }
				.orElse(null)
	}

	private Optional resolveFieldInTypeScope(QualifiedType qualifiedType, SimpleName symbol) {
		return storage.getCompilationInfo(qualifiedType).map { CompilationInfo otherInfo ->
			classLevelSolver.resolveFieldSymbol(symbol, qualifiedType, otherInfo).orElseGet {
				def typeDeclaration = otherInfo.innerClasses[qualifiedType] ?: otherInfo.mainType
				with(typeDeclaration) {
					def declaration = typeDeclaration as ClassOrInterfaceDeclaration
					resolveInheritance(declaration.extendedTypes, otherInfo, symbol) ?:
							resolveInheritance(declaration.implementedTypes, otherInfo, symbol)
				}
			}
		}
	}

	private static <T> T with(TypeDeclaration object, @ClosureParams(FirstParam.class) Closure<T> block) {
		if (object && object instanceof ClassOrInterfaceDeclaration) {
			return block.call(object)
		} else null
	}

	private Optional resolveMethodInTypeScope(QualifiedType qualifiedType, SimpleName symbol, MethodCallExpr maybeCallExpr) {
		return storage.getCompilationInfo(qualifiedType).map { CompilationInfo otherInfo ->
			classLevelSolver.resolveMethodSymbol(symbol, maybeCallExpr, qualifiedType, otherInfo).orElseGet {
				def typeDeclaration = otherInfo.innerClasses[qualifiedType] ?: otherInfo.mainType
				with(typeDeclaration) {
					def declaration = typeDeclaration as ClassOrInterfaceDeclaration
					resolveInheritance(declaration.extendedTypes, otherInfo, symbol, maybeCallExpr) ?:
							resolveInheritance(declaration.implementedTypes, otherInfo, symbol, maybeCallExpr)
				}
			}
		}
	}

	private FieldSymbolReference resolveInheritance(NodeList<ClassOrInterfaceType> extendedTypes,
													CompilationInfo info, SimpleName symbol) {
		return resolveInheritance(info, extendedTypes) { QualifiedType type, CompilationInfo otherInfo ->
			classLevelSolver.resolveFieldSymbol(symbol, type, otherInfo).orElse(null)
		} as FieldSymbolReference
	}

	private Object resolveInheritance(CompilationInfo info,
									  NodeList<ClassOrInterfaceType> extendsTypes,
									  Closure scope) {
		return extendsTypes.stream()
				.map { resolver.getQualifiedType(info.data, it) }
				.filter { !it.isUnknown() }
				.map { QualifiedType parentType ->
			storage.getCompilationInfo(parentType).map {
				scope.call(parentType, it)
			}.orElse(null)
		}.filter { it != null }.findFirst().orElse(null)
	}

	private MethodSymbolReference resolveInheritance(NodeList<ClassOrInterfaceType> extendedTypes,
													 CompilationInfo info, SimpleName symbol,
													 MethodCallExpr maybeCallExpr) {
		return resolveInheritance(info, extendedTypes) { QualifiedType type, CompilationInfo otherInfo ->
			classLevelSolver.resolveMethodSymbol(symbol, maybeCallExpr, type, otherInfo).orElse(null)
		} as MethodSymbolReference
	}

}
