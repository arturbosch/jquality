package com.gitlab.artismarti.smartsmells.deadcode

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.VariableDeclarationExpr

/**
 * @author artur
 */
class LocaleVariableHelper {

	static List<VariableDeclarationExpr> find(List<MethodDeclaration> methodDeclarations) {
		methodDeclarations.stream().map {
			def visitor = new LocaleVariableFinder()
			it.accept(visitor, null)
			visitor.variables
		}.flatMap({ it.stream() })
				.collect()
	}

	static List<VariableDeclarationExpr> find(MethodDeclaration methodDeclaration) {
		def visitor = new LocaleVariableFinder()
		methodDeclaration.accept(visitor, null)
		visitor.variables
	}
}
