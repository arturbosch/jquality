package com.gitlab.artismarti.smartsmells.godclass

import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
/**
 * @author artur
 */
class FieldAccessVisitor extends VoidVisitorAdapter<Set<String>> {

	private Set<String> fieldNames = new ArrayList<>()

	@Override
	void visit(FieldAccessExpr n, Set<String> arg) {
		fieldNames.add(n.field)
	}

	@Override
	void visit(MethodCallExpr n, Set<String> arg) {
		fieldNames.add(n.scope.toStringWithoutComments())
	}

	Set<String> getFieldNames() {
		return fieldNames
	}
}
