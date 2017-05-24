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

	def "all metric raisers can be combined through composite metric raiser"() {
		given: "all metric raisers"
		def lm = new LM()
		def lpl = new LPL()
		def wmc = new WMC()
		def atfd = new ATFD()
		def tcc = new TCC()
		def mcc = new MCCabe()
		def loc = new LOC()
		def sloc = new SLOC()
		def noa = new NOA()
		def nom = new NOM()
		def composite = new SimpleCompositeMetricRaiser([lm, lpl, wmc, atfd, tcc, mcc, loc, sloc, nom, noa])
		def metricRaiser = new CombinedCompositeMetricRaiser([composite])
		when: "querying all classes of godclass dummy"
		def godClass = Test.compile(Test.GOD_CLASS_DUMMY_PATH)
				.getChildNodesByType(ClassOrInterfaceDeclaration.class)[0]
		def metrics = metricRaiser.raise(godClass)
		then: "10 single metrics"
		metrics.size() == 10
	}

}
