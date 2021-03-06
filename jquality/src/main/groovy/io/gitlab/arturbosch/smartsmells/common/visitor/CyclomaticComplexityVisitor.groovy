package io.gitlab.arturbosch.smartsmells.common.visitor

import com.github.javaparser.ast.expr.ConditionalExpr
import com.github.javaparser.ast.stmt.DoStmt
import com.github.javaparser.ast.stmt.ForStmt
import com.github.javaparser.ast.stmt.ForeachStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.SwitchEntryStmt
import com.github.javaparser.ast.stmt.SwitchStmt
import com.github.javaparser.ast.stmt.TryStmt
import com.github.javaparser.ast.stmt.WhileStmt
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

/**
 * Attention! You have to add one to McCabeComplexity.
 * @author artur
 */
class CyclomaticComplexityVisitor extends VoidVisitorAdapter<Object> {

	private int mcCabeComplexity = 0

	private void incMcc() {
		mcCabeComplexity++
	}

	@Override
	void visit(IfStmt n, Object arg) {
		incMcc()
		super.visit(n, arg)
	}

	@Override
	void visit(ConditionalExpr n, Object arg) {
		incMcc()
		super.visit(n, arg)
	}

	@Override
	void visit(SwitchStmt n, Object arg) {
		super.visit(n, arg)
	}

	@Override
	void visit(SwitchEntryStmt n, Object arg) {
		if (!isDefaultCase(n))
			incMcc()
		super.visit(n, arg)
	}

	private static boolean isDefaultCase(SwitchEntryStmt n) {
		n.toString().contains("default")
	}

	@Override
	void visit(TryStmt n, Object arg) {
		incMcc()
		super.visit(n, arg)
	}

	@Override
	void visit(WhileStmt n, Object arg) {
		incMcc()
		super.visit(n, arg)
	}

	@Override
	void visit(DoStmt n, Object arg) {
		incMcc()
		super.visit(n, arg)
	}

	@Override
	void visit(ForeachStmt n, Object arg) {
		incMcc()
		super.visit(n, arg)
	}

	@Override
	void visit(ForStmt n, Object arg) {
		incMcc()
		super.visit(n, arg)
	}

	int getMcCabeComplexity() {
		return mcCabeComplexity
	}
}
