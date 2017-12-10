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

	static CompositeMetricRaiser create(boolean skipCCCM = false) {
		def lm = new LM()
		def lpl = new LPL()
		def atfd = new ATFD()
		def tcc = new TCC()
		def noa = new NOA()
		def nom = new NOM()
		def list = [atfd, tcc, nom, noa, lm, lpl]
		if (!skipCCCM) list.addAll(new CC(), new CM())
		def composite = new SimpleCompositeMetricRaiser(list as List<MetricRaiser>)
		return new CombinedCompositeMetricRaiser([composite, new LOC()] as List<CompositeMetricRaiser>)
	}

	private FullstackMetrics() {
		throw new IllegalStateException()
	}
}
