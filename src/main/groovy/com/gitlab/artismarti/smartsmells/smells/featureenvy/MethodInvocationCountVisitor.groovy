package com.gitlab.artismarti.smartsmells.smells.featureenvy

import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

/**
 * @author artur
 */
class MethodInvocationCountVisitor extends VoidVisitorAdapter {

	private int count
	private String searchedName

	MethodInvocationCountVisitor() {
		this.searchedName = ""
	}

	MethodInvocationCountVisitor(String searchedName) {
		this.searchedName = searchedName
	}

	/**
	 * Allows to count all invocations within the method or only invocations for a
	 * specific variable using its name as the searched name provided within the constructor.
	 *
	 * @param n the method within invocations are count
	 * @param arg store additional data here
	 */
	@Override
	void visit(MethodCallExpr n, Object arg) {
		if (searchedName.isEmpty()) {
			count++
		} else {
			Optional.ofNullable(n.scope)
					.map { it.toStringWithoutComments() }
					.filter { it.equals(searchedName) }
					.ifPresent { count++ }
		}
		super.visit(n, arg)
	}

	int getCount() {
		return count
	}
}
