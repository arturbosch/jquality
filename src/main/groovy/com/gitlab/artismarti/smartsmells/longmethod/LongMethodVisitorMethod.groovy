package com.gitlab.artismarti.smartsmells.longmethod

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.Statement
import com.gitlab.artismarti.smartsmells.common.MethodMetricVisitor
/**
 * @author artur
 */
class LongMethodVisitorMethod extends MethodMetricVisitor<LongMethod> {

	LongMethodVisitorMethod(int threshold) {
		super(threshold)
	}

	@Override
	protected byThreshold(MethodDeclaration n, List<Statement> stmt) {
		return stmt.size() > threshold
	}

	protected def addSmell(MethodDeclaration n, List<Statement> it) {
		smells.add(newLongMethod(n, it))
	}
}
