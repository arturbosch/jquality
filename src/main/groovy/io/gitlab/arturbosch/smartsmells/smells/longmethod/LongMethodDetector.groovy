package io.gitlab.arturbosch.smartsmells.smells.longmethod

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
import io.gitlab.arturbosch.smartsmells.metrics.internal.LOC
import io.gitlab.arturbosch.smartsmells.metrics.raisers.LOC
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
@CompileStatic
class LongMethodDetector extends Detector<LongMethod> {

	private int threshold

	LongMethodDetector(int threshold = Defaults.LONG_METHOD) {
		this.threshold = threshold
	}

	@Override
	protected Visitor getVisitor() {
		new LongMethodVisitor(threshold)
	}

	@Override
	Smell getType() {
		return Smell.LONG_METHOD
	}
}

@CompileStatic
class LongMethodVisitor extends MethodMetricVisitor<LongMethod> {

	LongMethodVisitor(int threshold) {
		super(threshold)
	}

	@Override
	protected void callback(CallableDeclaration n, Resolver arg) {
		def loc = current?.getMethodByDeclaration(n)?.getMetric(LOC.SLOC)?.value ?: 0
		if (loc > threshold) {
			report(new LongMethod(n.nameAsString, n.declarationAsString, loc, threshold,
					SourceRange.fromNode(n), SourcePath.of(info), ElementTarget.METHOD))
		}
	}
}
