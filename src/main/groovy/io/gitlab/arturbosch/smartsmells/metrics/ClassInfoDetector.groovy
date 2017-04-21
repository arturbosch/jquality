package io.gitlab.arturbosch.smartsmells.metrics

import io.gitlab.arturbosch.smartsmells.api.CompositeMetricRaiser
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.internal.FullstackMetrics

/**
 * @author artur
 */
class ClassInfoDetector extends Detector<ClassInfo> {

	private final CompositeMetricRaiser metrics

	ClassInfoDetector(final boolean skipCC_CM = false) {
		metrics = FullstackMetrics.create(skipCC_CM)
	}

	ClassInfoDetector(final CompositeMetricRaiser compositeMetricRaiser) {
		metrics = compositeMetricRaiser
	}

	@Override
	protected Visitor getVisitor() {
		return new ClassInfoVisitor(metrics)
	}

	@Override
	Smell getType() {
		return Smell.CLASS_INFO
	}
}
