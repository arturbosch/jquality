package io.gitlab.arturbosch.smartsmells.smells.statechecking

import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.SwitchStmt
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor

/**
 * @author Artur Bosch
 */
@CompileStatic
class StateCheckingVisitor extends Visitor<StateChecking> {

	@Override
	void visit(SwitchStmt n, Resolver arg) {
		println n.toString()
		super.visit(n, arg)
	}

	@Override
	void visit(IfStmt n, Resolver arg) {
		println n.toString()
		super.visit(n, arg)
	}

}
