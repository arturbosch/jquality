package com.gitlab.artismarti.smartsmells.common.visitor

import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
/**
 * @author artur
 */
class LocaleVariableVisitor extends VoidVisitorAdapter<Object> {

	def variables = new HashSet<VariableDeclarationExpr>()

	@Override
	void visit(VariableDeclarationExpr n, Object arg) {
		n.each { variables.add(it) }
	}

}
