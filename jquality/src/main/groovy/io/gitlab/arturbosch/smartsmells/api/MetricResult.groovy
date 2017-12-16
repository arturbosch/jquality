package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
@CompileStatic
class MetricResult implements DetectionResult {

	final List<Metric> metrics

	MetricResult(List<Metric> metrics) {
		this.metrics = metrics
	}

	@Override
	String toString() {
		return metrics.join("\n")
	}

	@Override
	String asCompactString() {
		return toString()
	}

	@Override
	String asComparableString() {
		return toString()
	}

	@Override
	ElementTarget elementTarget() {
		return ElementTarget.NOT_SPECIFIED
	}
}
