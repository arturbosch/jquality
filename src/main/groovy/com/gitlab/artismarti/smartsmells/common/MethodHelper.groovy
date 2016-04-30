package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.stmt.ReturnStmt
import com.github.javaparser.ast.stmt.Statement
import com.gitlab.artismarti.smartsmells.complexmethod.CyclomaticComplexityVisitor
import com.gitlab.artismarti.smartsmells.featureenvy.MethodInvocationCountVisitor

/**
 * @author artur
 */
class MethodHelper {

	static int calcMcCabe(BodyDeclaration n) {
		return mcCabeIntern(n) + 1
	}

	static int calcMcCabeForStatement(Statement n) {
		return mcCabeIntern(n)
	}

	private static int mcCabeIntern(Node n) {
		def complexityVisitor = new CyclomaticComplexityVisitor()
		n.accept(complexityVisitor, null)
		return complexityVisitor.mcCabeComplexity
	}

	static List<Parameter> extractParameters(BodyDeclaration n) {
		return n instanceof ConstructorDeclaration ?
				n.parameters : ((MethodDeclaration) n).parameters
	}

	static boolean isGetterOrSetter(List<Statement> stmts) {
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

	static int getAllMethodInvocations(MethodDeclaration it) {
		def visitor = new MethodInvocationCountVisitor()
		executeVisitor(it, visitor)
	}

	private static int executeVisitor(MethodDeclaration it, MethodInvocationCountVisitor visitor) {
		it.accept(visitor, null)
		visitor.count
	}

	static int getAllMethodInvocationsForEntityWithName(String searchedName, MethodDeclaration it) {
		def visitor = new MethodInvocationCountVisitor(searchedName)
		executeVisitor(it, visitor)
	}

	static boolean sizeBiggerThan(int threshold, MethodDeclaration methodDeclaration) {
		return Optional.ofNullable(methodDeclaration.body).filter { it.stmts.size() > threshold }.isPresent()
	}
}
