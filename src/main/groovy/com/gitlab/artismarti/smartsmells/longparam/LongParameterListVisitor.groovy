package com.gitlab.artismarti.smartsmells.longparam

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.Statement
import com.gitlab.artismarti.smartsmells.common.MethodMetricVisitor

/**
 * @author artur
 */
class LongParameterListVisitor extends MethodMetricVisitor<LongParameterList> {

	LongParameterListVisitor(int threshold) {
		super(threshold)
	}

	@Override
	protected byThreshold(MethodDeclaration n, List<Statement> stmt) {
		return n.parameters.size() > threshold
	}

	@Override
	protected addSmell(MethodDeclaration n, List<Statement> it) {
		smells.add(new LongParameterList(newLongMethod(n, it),
				n.parameters.collect { it.toStringWithoutComments() }))
	}
}
