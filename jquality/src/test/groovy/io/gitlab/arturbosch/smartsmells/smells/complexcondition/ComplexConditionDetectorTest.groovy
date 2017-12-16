package io.gitlab.arturbosch.smartsmells.smells.complexcondition

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class ComplexConditionDetectorTest extends Specification {

	def "find one complex condition"() {
		expect:
		smells.size() == 3
		smells[0].signature == "5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)"

		where:
		smells = new ComplexConditionDetector().run(Test.COMPLEX_CONDITION_DUMMY_PATH)
	}

}
