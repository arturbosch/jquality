package io.gitlab.arturbosch.jpal.resolution.solvers

import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.expr.SimpleName
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.symbols.SimpleSymbolReference
import io.gitlab.arturbosch.jpal.resolution.symbols.SymbolReference

/**
 * @author Artur Bosch
 */
@CompileStatic
final class ObjectCreationSymbolSolver extends CallOrAccessAwareSolver implements Solver {

	private TypeSolver resolver

	ObjectCreationSymbolSolver(TypeSolver resolver) {
		this.resolver = resolver
	}

	@Override
	Optional<? extends SymbolReference> resolve(SimpleName symbol, CompilationInfo info) {
		def access = asFieldAccess(symbol)
		if (access) return resolveInFieldAccess(symbol, access, info)
		def call = asMethodCall(symbol)
		if (call) return resolveInMethodCall(symbol, call, info)
		return Optional.empty()
	}

	Optional<SimpleSymbolReference> resolveInFieldAccess(SimpleName symbol, FieldAccessExpr fieldAccessExpr,
														 CompilationInfo info) {
		resolveInScope(symbol, Optional.of(fieldAccessExpr.scope), info)
	}

	private Optional<SimpleSymbolReference> resolveInScope(SimpleName symbol,
														   Optional<Expression> scope,
														   CompilationInfo info) {
		return scope.filter { it instanceof ObjectCreationExpr }
				.map { it as ObjectCreationExpr }
				.map { new SimpleSymbolReference(symbol, resolver.getQualifiedType(info.data, it.type)) }
	}

	Optional<SimpleSymbolReference> resolveInMethodCall(SimpleName symbol, MethodCallExpr methodCallExpr,
														CompilationInfo info) {
		resolveInScope(symbol, methodCallExpr.scope, info)
	}
}
