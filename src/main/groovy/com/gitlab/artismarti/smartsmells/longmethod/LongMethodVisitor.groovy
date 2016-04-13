package com.gitlab.artismarti.smartsmells.longmethod

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.Statement
import com.gitlab.artismarti.smartsmells.common.MethodMetricVisitor

import java.nio.file.Path

/**
 * @author artur
 */
class LongMethodVisitor extends MethodMetricVisitor<LongMethod> {

	LongMethodVisitor(int threshold, Path path) {
		super(threshold, path)
	}

	@Override
	protected byThreshold(MethodDeclaration n, List<Statement> stmt) {
		return stmt.size() > threshold
	}

	protected def addSmell(MethodDeclaration n, List<Statement> it) {
		smells.add(newLongMethod(n, it))
	}
}
