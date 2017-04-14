package io.gitlab.arturbosch.smartsmells.metrics.internal

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class MetricRaiserTest extends Specification {

	def "raise metrics for dummies"() {
		given: "composite metric raiser for all supported metrics"
		def lm = new LongMethodAverageAndDeviation()
		def lpl = new LongParameterListAverageAndDeviation()
		def wmc = new WMC()
		def atfd = new ATFD()
		def mcc = new MCCabe()
		def loc = new LOC()
		def sloc = new SLOC()
		def composite = new SimpleCompositeMetricRaiser([wmc, atfd, mcc, loc, sloc])
		def metricRaiser = new CombinedCompositeMetricRaiser([lm, lpl, composite])
		when: "querying all classes"
		def classes = Test.compile(Test.GOD_CLASS_DUMMY_PATH)
				.getNodesByType(ClassOrInterfaceDeclaration.class)
		classes.collect { metricRaiser.raise(it) }.each { println it }
		then:
		true
	}

}
