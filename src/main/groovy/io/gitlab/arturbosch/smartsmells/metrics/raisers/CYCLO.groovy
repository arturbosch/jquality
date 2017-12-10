package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.CallableDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.metrics.Metrics

/**
 * @author Artur Bosch
 */
@CompileStatic
class CYCLO implements MethodMetricRaiser {

	static final String CYCLOMATIC_COMPLEXITY = "CYCLO"

	@Override
	String name() {
		return CYCLOMATIC_COMPLEXITY
	}

	@Override
	Metric raise(CallableDeclaration method, Resolver resolver) {
		def mcCabe = Metrics.mcCabe(method)
		return Metric.of(CYCLOMATIC_COMPLEXITY, mcCabe)
	}
}
