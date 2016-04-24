package com.gitlab.artismarti.smartsmells.featureenvy

import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

/**
 * @author artur
 */
class MethodInvocationCountVisitor extends VoidVisitorAdapter {

	private int count

	@Override
	void visit(MethodCallExpr n, Object arg) {
		count++
		super.visit(n, arg)
	}

	int getCount() {
		return count
	}
}
