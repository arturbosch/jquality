package io.gitlab.arturbosch.smartsmells.common.helper

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import io.gitlab.arturbosch.smartsmells.common.visitor.LocaleVariableVisitor

/**
 * @author artur
 */
class LocaleVariableHelper {

	static List<VariableDeclarationExpr> find(List<MethodDeclaration> methodDeclarations) {
		methodDeclarations.stream().map {
			def visitor = new LocaleVariableVisitor()
			it.accept(visitor, null)
			visitor.variables
		}.flatMap({ it.stream() })
				.collect()
	}

	static Set<VariableDeclarationExpr> find(MethodDeclaration methodDeclaration) {
		def visitor = new LocaleVariableVisitor()
		methodDeclaration.accept(visitor, null)
		visitor.variables
	}
}
