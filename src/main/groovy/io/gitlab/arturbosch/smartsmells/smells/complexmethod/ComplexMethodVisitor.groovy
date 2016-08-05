package io.gitlab.arturbosch.smartsmells.smells.complexmethod

import com.github.javaparser.ast.body.BodyDeclaration
import io.gitlab.arturbosch.smartsmells.common.visitor.MethodMetricVisitor
import io.gitlab.arturbosch.smartsmells.metrics.Metrics

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
	protected byThreshold(BodyDeclaration n) {
		mcc = Metrics.mcCabe(n)
		return mcc >= threshold
	}

	@Override
	protected addSmell(BodyDeclaration n) {
		smells.add(new ComplexMethod(newLongMethod(n, mcc), mcc))
	}
}
