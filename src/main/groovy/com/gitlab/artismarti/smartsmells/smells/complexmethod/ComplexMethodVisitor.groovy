package com.gitlab.artismarti.smartsmells.smells.complexmethod

import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.stmt.Statement
import com.gitlab.artismarti.smartsmells.common.helper.MethodHelper
import com.gitlab.artismarti.smartsmells.common.visitor.MethodMetricVisitor

import java.nio.file.Path

/**
 * @author artur
 */
class ComplexMethodVisitor extends MethodMetricVisitor<ComplexMethod> {

	int mcc

	ComplexMethodVisitor(int threshold, Path path) {
		super(threshold, path)
	}

	@Override
	protected byThreshold(BodyDeclaration n, List<Statement> stmt) {
		mcc = MethodHelper.calcMcCabe(n)
		return mcc >= threshold
	}

	@Override
	protected addSmell(BodyDeclaration n, List<Statement> stmt) {
		smells.add(new ComplexMethod(newLongMethod(n, stmt), mcc))
	}
}
