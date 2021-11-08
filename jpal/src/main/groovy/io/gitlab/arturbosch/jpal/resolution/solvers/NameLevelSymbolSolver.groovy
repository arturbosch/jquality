package io.gitlab.arturbosch.jpal.resolution.solvers

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.type.ClassOrInterfaceType
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.symbols.SymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.TypeSymbolReference

/**
 * @author Artur Bosch
 */
@CompileStatic
final class NameLevelSymbolSolver implements Solver {

	private TypeSolver resolver
	private CompilationStorage storage
	private MethodLevelVariableSymbolSolver methodLevelSolver

	NameLevelSymbolSolver(CompilationStorage storage, TypeSolver resolver,
						  MethodLevelVariableSymbolSolver methodLevelSolver) {
		this.storage = storage
		this.resolver = resolver
		this.methodLevelSolver = methodLevelSolver
	}

	@Override
	Optional<? extends SymbolReference> resolve(SimpleName symbol, CompilationInfo info) {
		return Optional.ofNullable(resolveNameExprScope(symbol.parentNode, info))
	}

	SymbolReference resolveNameExprScope(Optional<Node> scope, CompilationInfo info) {
		return scope.filter { it instanceof NameExpr }
				.map { it as NameExpr }
				.map { it.name }
				.map { resolveStaticOrIdentifier(it, info) }
				.filter { it.isPresent() }
				.map { it.get() }
				.orElse(null)
	}

	private Optional<? extends SymbolReference> resolveStaticOrIdentifier(SimpleName symbol, CompilationInfo info) {
		def isStatic = Character.isUpperCase(symbol.identifier.charAt(0))
		def qualifiedType = resolver.getQualifiedType(info.data, new ClassOrInterfaceType(symbol.identifier))
		return isStatic ? resolveStaticType(symbol, qualifiedType) : methodLevelSolver.resolve(symbol, info)
	}

	private Optional<? extends SymbolReference> resolveStaticType(SimpleName symbol, QualifiedType qualifiedType) {
		storage.getCompilationInfo(qualifiedType)
				.map { it.innerClasses[qualifiedType] ?: it.mainType }
				.map { new TypeSymbolReference(symbol, qualifiedType, it) }
	}
}
