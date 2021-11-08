package io.gitlab.arturbosch.jpal.resolution.solvers

import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.SimpleName
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.symbols.FieldSymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.MethodSymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.SymbolReference

/**
 * @author Artur Bosch
 */
@CompileStatic
final class LocalClassLevelSymbolSolver extends CallOrAccessAwareSolver implements Solver {

	private TypeSolver resolver

	LocalClassLevelSymbolSolver(TypeSolver resolver) {
		this.resolver = resolver
	}

	@Override
	Optional<? extends SymbolReference> resolve(SimpleName symbol, CompilationInfo info) {
		// first a symbol can be a field access of 'this' or some other symbol
		def maybeFieldAccess = asFieldAccess(symbol)
		if (maybeFieldAccess && isThisAccess(maybeFieldAccess)) {
			return resolveFieldSymbol(symbol, info.qualifiedType, info)
		}

		// like fields accesses, method calls can be located in 'this' resolution data
		def maybeCallExpr = asMethodCall(symbol)
		if (maybeCallExpr && isThisAccess(maybeCallExpr)) {
			return resolveMethodSymbol(symbol, maybeCallExpr, info.qualifiedType, info)
		}

		return Optional.empty()
	}

	Optional<FieldSymbolReference> resolveFieldSymbol(SimpleName symbol, QualifiedType searchScope, CompilationInfo info) {

		def fields = Optional.ofNullable(info.innerClasses[searchScope]).orElse(info.mainType).fields
		def maybe = fields.find { it.variables.find { it.name == symbol } }
		if (maybe) {
			def qualifiedType = resolver.getQualifiedType(info.data, maybe.commonType)
			if (qualifiedType != QualifiedType.UNKNOWN) {
				return Optional.of(new FieldSymbolReference(symbol, qualifiedType, maybe))
			}
		}
		return Optional.empty()
	}

	Optional<MethodSymbolReference> resolveMethodSymbol(SimpleName symbol, MethodCallExpr methodCall,
														QualifiedType searchScope, CompilationInfo info) {
		def methods = Optional.ofNullable(info.innerClasses[searchScope]).orElse(info.mainType).methods
		def numberOfArguments = methodCall.arguments.size()
		def maybe = methods.find {
			def numberOfParameters = it.parameters.size()
			// TODO evaluate arguments and parameters
			it.name == symbol && numberOfArguments == numberOfParameters
		}
		if (maybe) {
			def qualifiedType = resolver.getQualifiedType(info.data, maybe.type)
			if (qualifiedType != QualifiedType.UNKNOWN) {
				return Optional.of(new MethodSymbolReference(symbol, qualifiedType, maybe))
			}
		}

		return Optional.empty()
	}
}
