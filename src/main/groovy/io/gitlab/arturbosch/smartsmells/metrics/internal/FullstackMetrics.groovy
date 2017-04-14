package io.gitlab.arturbosch.smartsmells.metrics.internal

/**
 * @author Artur Bosch
 */
class FullstackMetrics {

	static CompositeMetricRaiser create(boolean skipCCCM = false) {
		def lm = new LongMethodAverageAndDeviation()
		def lpl = new LongParameterListAverageAndDeviation()
		def wmc = new WMC()
		def atfd = new ATFD()
		def tcc = new TCC()
		def mcc = new MCCabe()
		def loc = new LOC()
		def sloc = new SLOC()
		def noa = new NOA()
		def nom = new NOM()
		def list = [wmc, atfd, tcc, mcc, loc, sloc, nom, noa]
		if (!skipCCCM) list.addAll(new CC(), new CM())
		def composite = new SimpleCompositeMetricRaiser(list)
		return new CombinedCompositeMetricRaiser([lm, lpl, composite])
	}

	private FullstackMetrics() {
		throw new IllegalStateException()
	}
}
