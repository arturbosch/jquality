package io.gitlab.arturbosch.jpal.ast.visitors

import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.CompileStatic

/**
 * Visits method call expressions and count them if they match the searched name or
 * just all invocations.
 *
 * @author artur
 */
@CompileStatic
class MethodInvocationCountVisitor extends VoidVisitorAdapter {

	private int count
	private Set<String> testedMethods = new HashSet<>()
	private final String searchedName

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
			if (!(n.nameAsString in testedMethods)) {
				def scopeIsSearchedName = n.scope
						.filter { it instanceof NameExpr }
						.map { it as NameExpr }
						.map { it.nameAsString }
						.filter { it == searchedName }
				if (scopeIsSearchedName.isPresent()) {
					count++
					testedMethods.add(n.nameAsString)
				}
			}
		}
		super.visit(n, arg)
	}

	int getCount() {
		return count
	}
}
