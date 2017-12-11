package io.gitlab.arturbosch.smartsmells.smells.longparam

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
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NOP
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
@CompileStatic
class LongParameterListDetector extends Detector<LongParameterList> {

	private int threshold

	LongParameterListDetector(int threshold = Defaults.LONG_PARAMETER_LIST) {
		this.threshold = threshold
	}

	@Override
	protected Visitor getVisitor() {
		return new LongParameterListVisitor(threshold)
	}

	@Override
	Smell getType() {
		return Smell.LONG_PARAM
	}
}

@CompileStatic
class LongParameterListVisitor extends MethodMetricVisitor<LongParameterList> {

	private int threshold

	LongParameterListVisitor(int threshold) {
		this.threshold = threshold
	}

	@Override
	protected void callback(CallableDeclaration n, Resolver arg) {
		def nop = current?.getMethodByDeclaration(n)?.getMetric(NOP.NUMBER_OF_PARAMETERS)?.value ?: 0
		if (nop > threshold) {
			report(new LongParameterList(n.nameAsString, n.declarationAsString, nop, threshold,
					n.parameters.collect { it.nameAsString },
					SourceRange.fromNode(n), SourcePath.of(info), ElementTarget.METHOD))
		}
	}
}
