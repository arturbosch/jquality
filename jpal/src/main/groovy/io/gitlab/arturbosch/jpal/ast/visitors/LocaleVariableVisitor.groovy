package io.gitlab.arturbosch.jpal.ast.visitors

import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.CompileStatic

/**
 * @author artur
 */
@CompileStatic
class LocaleVariableVisitor extends VoidVisitorAdapter<Object> {

	final Set<VariableDeclarationExpr> variables = new HashSet<VariableDeclarationExpr>()

	@Override
	void visit(VariableDeclarationExpr n, Object arg) {
		variables.add(n)
	}

}
