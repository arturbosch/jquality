package io.gitlab.arturbosch.smartsmells.metrics.internal

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.CombinedCompositeMetricRaiser
import io.gitlab.arturbosch.smartsmells.api.CompositeMetricRaiser
import io.gitlab.arturbosch.smartsmells.api.MetricRaiser
import io.gitlab.arturbosch.smartsmells.api.SimpleCompositeMetricRaiser

/**
 * @author Artur Bosch
 */
@CompileStatic
class FullstackMetrics {

	static CompositeMetricRaiser create() {
		def noa = new NOA()
		def nom = new NOM()
		def list = [nom, noa]
		def composite = new SimpleCompositeMetricRaiser(list as List<MetricRaiser>)
		return new CombinedCompositeMetricRaiser([composite] as List<CompositeMetricRaiser>)
	}

	private FullstackMetrics() {
		throw new IllegalStateException()
	}
}
