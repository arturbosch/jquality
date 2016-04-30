package com.gitlab.artismarti.smartsmells.common.visitor

import com.github.javaparser.ast.expr.ConditionalExpr
import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.gitlab.artismarti.smartsmells.common.helper.MethodHelper

/**
 * Attention! You have to add one to McCabeComplexity
 * @author artur
 */
class CyclomaticComplexityVisitor extends VoidVisitorAdapter<Object> {

	private int mcCabeComplexity = 0

	@Override
	void visit(IfStmt n, Object arg) {
		incAndCalcMcc(n.thenStmt)
		calcMcc(n.elseStmt)
	}

	private void incAndCalcMcc(Statement statement) {
		incMcc()
		calcMcc(statement)
	}

	private void incMcc() {
		mcCabeComplexity++
	}

	private void calcMcc(Statement statement) {
		Optional.ofNullable(statement)
				.ifPresent({ mcCabeComplexity += MethodHelper.calcMcCabeForStatement(it) })
	}

	@Override
	void visit(ConditionalExpr n, Object arg) {
		incMcc()
	}

	@Override
	void visit(SwitchStmt n, Object arg) {
		n.entries.forEach({ calcMcc(it) })
	}

	@Override
	void visit(SwitchEntryStmt n, Object arg) {
		if (!isDefaultCase(n))
			incMcc()
		controlFlowStatements(n.stmts)
				.forEach({ calcMcc(it) })
	}

	private static boolean isDefaultCase(SwitchEntryStmt n) {
		n.toString().contains("default")
	}

	static def controlFlowStatements(List<Statement> statements) {
		statements.grep({
			it instanceof DoStmt || it instanceof ForeachStmt ||
					it instanceof ForStmt || it instanceof IfStmt ||
					it instanceof ForStmt || it instanceof IfStmt ||
					it instanceof SwitchEntryStmt || it instanceof SwitchStmt ||
					it instanceof TryStmt || it instanceof WhileStmt
		})
	}

	@Override
	void visit(TryStmt n, Object arg) {
		calcMcc(n.tryBlock)
		n.catchs.forEach({ incAndCalcMcc(it.catchBlock) })
	}

	@Override
	void visit(WhileStmt n, Object arg) {
		incAndCalcMcc(n.body)
	}

	@Override
	void visit(DoStmt n, Object arg) {
		incAndCalcMcc(n.body)
	}

	@Override
	void visit(ForeachStmt n, Object arg) {
		incAndCalcMcc(n.body)
	}

	@Override
	void visit(ForStmt n, Object arg) {
		incAndCalcMcc(n.body)
	}

	int getMcCabeComplexity() {
		return mcCabeComplexity
	}
}
