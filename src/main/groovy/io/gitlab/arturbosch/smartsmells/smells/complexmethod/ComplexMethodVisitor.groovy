package io.gitlab.arturbosch.smartsmells.smells.complexmethod

import com.github.javaparser.ast.body.CallableDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.common.visitor.MethodMetricVisitor
import io.gitlab.arturbosch.smartsmells.metrics.Metrics

/**
 * @author Artur Bosch
 */
@CompileStatic
class ComplexMethodVisitor extends MethodMetricVisitor<ComplexMethod> {

	int mcc

	ComplexMethodVisitor(int threshold) {
		super(threshold)
	}

	@Override
	protected byThreshold(CallableDeclaration n) {
		mcc = Metrics.mcCabe(n)
		return mcc >= threshold
	}

	@Override
	protected addSmell(CallableDeclaration n) {
		report((ComplexMethod) ComplexMethod.of(newLongMethod(n, mcc)))
	}
}
