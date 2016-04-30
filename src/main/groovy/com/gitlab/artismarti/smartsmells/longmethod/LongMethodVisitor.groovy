package com.gitlab.artismarti.smartsmells.longmethod

import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.stmt.Statement
import com.gitlab.artismarti.smartsmells.common.visitor.MethodMetricVisitor

import java.nio.file.Path
/**
 * @author artur
 */
class LongMethodVisitor extends MethodMetricVisitor<LongMethod> {

	LongMethodVisitor(int threshold, Path path) {
		super(threshold, path)
	}

	@Override
	protected byThreshold(BodyDeclaration n, List<Statement> stmt) {
		return stmt.size() > threshold
	}

	protected def addSmell(BodyDeclaration n, List<Statement> it) {
		smells.add(newLongMethod(n, it))
	}
}
