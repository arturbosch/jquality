package io.gitlab.arturbosch.smartsmells.smells.complexmethod

import com.github.javaparser.ast.body.CallableDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
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

	ComplexMethodVisitor(int threshold) {
		super(threshold)
	}

	@Override
	protected void callback(CallableDeclaration n, Resolver arg) {
		int mcc = current?.getMethodByDeclaration(n)?.getMetric(CYCLO.CYCLOMATIC_COMPLEXITY)?.value ?: 0
		if (mcc > threshold) {
			report(new ComplexMethod(n.nameAsString, n.declarationAsString, mcc, threshold,
					SourceRange.fromNode(n), SourcePath.of(info), ElementTarget.METHOD))
		}
	}
}
