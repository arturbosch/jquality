package com.gitlab.artismarti.smartsmells.dataclass

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.gitlab.artismarti.smartsmells.common.MethodHelper

/**
 * @author artur
 */
class ClassVisitor extends VoidVisitorAdapter {

	private boolean isDataClass = true

	@Override
	void visit(MethodDeclaration n, Object arg) {
		if (n.getBody()) {
			def stmts = n.getBody().stmts
			if (stmts) {
				isDataClass &= MethodHelper.isGetterOrSetter(stmts)
			}
		}
	}

	boolean isDataClass() {
		return isDataClass
	}
}
