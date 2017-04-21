package io.gitlab.arturbosch.smartsmells.metrics.internal

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.smartsmells.api.CombinedCompositeMetricRaiser
import io.gitlab.arturbosch.smartsmells.api.SimpleCompositeMetricRaiser
import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class MetricRaiserTest extends Specification {

	def "raise metrics for godclass dummy"() {
		given: "composite metric raiser for all supported godClass"
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
		def composite = new SimpleCompositeMetricRaiser([wmc, atfd, tcc, mcc, loc, sloc, nom, noa])
		def metricRaiser = new CombinedCompositeMetricRaiser([lm, lpl, composite])
		when: "querying all godClass"
		def godClass = Test.compile(Test.GOD_CLASS_DUMMY_PATH)
				.getNodesByType(ClassOrInterfaceDeclaration.class)[0]
		def metrics = metricRaiser.raise(godClass)
		then: "8 godClass + 2 composite godClass (4) = 12"
		metrics.size() == 12
	}

}
