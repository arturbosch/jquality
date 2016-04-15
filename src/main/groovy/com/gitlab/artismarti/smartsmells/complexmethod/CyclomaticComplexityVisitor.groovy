package com.gitlab.artismarti.smartsmells.complexmethod

import com.github.javaparser.ast.expr.ConditionalExpr
import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.gitlab.artismarti.smartsmells.common.MethodHelper

/**
 * Attention! You have to add one to mccabe complexity
 * @author artur
 */
class CyclomaticComplexityVisitor extends VoidVisitorAdapter<Object> {

	private int mcCabeComplexity = 0

	@Override
	void visit(IfStmt n, Object arg) {
		println "if stmt"
		incMcc(n.thenStmt)
		incMcc(n.elseStmt)
	}

	private void incMcc(Statement statement) {
		Optional.ofNullable(statement)
				.ifPresent({ mcCabeComplexity += MethodHelper.calcMcCabeForStatement(it) })

	}

	@Override
	void visit(ConditionalExpr n, Object arg) {
		println "conditional stmt"
		mcCabeComplexity++
	}

	@Override
	void visit(SwitchStmt n, Object arg) {
		println "switch"
		n.entries.forEach({ incMcc(it) })
	}

	@Override
	void visit(SwitchEntryStmt n, Object arg) {
		println "switch case"
//		if (!isDefaultCase(n)) n.stmts.forEach({ incMcc(it) })
	}

	private static boolean isDefaultCase(SwitchEntryStmt n) {
		println n.label.toString()
		n.label.toString().contains("default")
	}

	@Override
	void visit(TryStmt n, Object arg) {
		println "try"
		incMcc(n.tryBlock)
		n.catchs.forEach({ incMcc(it.catchBlock) })
	}

	@Override
	void visit(WhileStmt n, Object arg) {
		println "while"
		incMcc(n.body)
	}

	@Override
	void visit(DoStmt n, Object arg) {
		println "do while"
		incMcc(n.body)
	}

	@Override
	void visit(ForeachStmt n, Object arg) {
		println "for each"
		incMcc(n.body)
	}

	@Override
	void visit(ForStmt n, Object arg) {
		println "for"
		incMcc(n.body)
	}

	int getMcCabeComplexity() {
		return mcCabeComplexity
	}
}
