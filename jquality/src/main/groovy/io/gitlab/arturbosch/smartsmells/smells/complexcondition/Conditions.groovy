package io.gitlab.arturbosch.smartsmells.smells.complexcondition

import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.EnclosedExpr
import com.github.javaparser.ast.expr.Expression
import io.gitlab.arturbosch.jpal.internal.Printer

/**
 * @author Artur Bosch
 */
class Conditions {

	private Conditions() {
	}

	static void whileBinaryExpression(Set<String> cases, Expression expression) {
		if (expression instanceof BinaryExpr && isAndOrXor(expression as BinaryExpr)) {
			def binaryExpr = expression as BinaryExpr
			whileBinaryExpression(cases, binaryExpr.left)
			whileBinaryExpression(cases, binaryExpr.right)
		} else if (expression instanceof EnclosedExpr) {
			whileBinaryExpression(cases, (expression as EnclosedExpr).inner)
		} else {
			cases.add(expression.toString(Printer.NO_COMMENTS))
		}
	}

	static boolean isAndOrXor(BinaryExpr binaryExpr) {
		def operator = binaryExpr.operator
		return operator == BinaryExpr.Operator.OR || operator == BinaryExpr.Operator.AND ||
				operator == BinaryExpr.Operator.XOR
	}
}
