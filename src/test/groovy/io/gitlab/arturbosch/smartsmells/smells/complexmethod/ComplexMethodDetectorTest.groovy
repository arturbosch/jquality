package io.gitlab.arturbosch.smartsmells.smells.complexmethod

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class ComplexMethodDetectorTest extends Specification {

	def "finds one complex method"() {
		expect:
		smells.size() == 1
		smells[0].cyclomaticComplexity == 11
		smells[0].longMethod.name == "complexMethod"

		where:
		smells = new ComplexMethodDetector().run(Test.COMPLEX_METHOD_DUMMY_PATH)
	}
}
