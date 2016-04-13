package com.gitlab.artismarti.smartsmells.dataclass

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.stmt.ReturnStmt
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
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
				isDataClass &= isGetterOrSetter(stmts)
			}
		}
	}

	private static boolean isGetterOrSetter(List<Statement> stmts) {
		if (stmts.size() == 1) {
			def statement = stmts.get(0)
			return isGetter(statement) || isSetter(statement)
		}
		return false
	}

	static boolean isSetter(Statement statement) {
		if (statement instanceof ExpressionStmt) {
			def expression = ((ExpressionStmt) statement).expression
			if (expression instanceof AssignExpr) {
				return true
			}
		}
		return false
	}

	static boolean isGetter(Statement statement) {
		if (statement instanceof ReturnStmt) {
			def expression = ((ReturnStmt) statement).expr
			if (expression instanceof NameExpr) {
				return true
			}
		}
		return false
	}

	def boolean isDataClass() {
		return isDataClass
	}
}
