package io.gitlab.arturbosch.smartsmells.smells.complexmethod

import com.github.javaparser.ast.body.CallableDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.common.visitor.MethodMetricVisitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.raisers.CYCLO
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
@CompileStatic
class ComplexMethodDetector extends Detector<ComplexMethod> {

	int complexity

	ComplexMethodDetector(int complexityThreshold = Defaults.COMPLEX_METHOD) {
		this.complexity = complexityThreshold
	}

	@Override
	protected Visitor getVisitor() {
		return new ComplexMethodVisitor(complexity)
	}

	@Override
	Smell getType() {
		return Smell.COMPLEX_METHOD
	}
}

@CompileStatic
class ComplexMethodVisitor extends MethodMetricVisitor<ComplexMethod> {

	private int threshold

	ComplexMethodVisitor(int threshold) {
		this.threshold = threshold
	}

	@Override
	protected void callback(CallableDeclaration n, Resolver arg) {
		def methodInfo = current?.getMethodByDeclaration(n)
		int mcc = methodInfo?.getMetric(CYCLO.CYCLOMATIC_COMPLEXITY)?.value ?: 0
		if (methodInfo && mcc > threshold) {
			report(new ComplexMethod(methodInfo.name, methodInfo.signature, mcc, threshold,
					methodInfo.sourceRange, methodInfo.sourcePath, ElementTarget.METHOD))
		}
	}
}
