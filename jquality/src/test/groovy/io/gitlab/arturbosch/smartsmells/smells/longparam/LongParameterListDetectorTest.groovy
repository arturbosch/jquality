package io.gitlab.arturbosch.smartsmells.smells.longparam

import io.gitlab.arturbosch.smartsmells.DetectorSpecification
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Test

/**
 * @author artur
 */
class LongParameterListDetectorTest extends DetectorSpecification<LongParameterList> {

	@Override
	Detector<LongParameterList> detector() {
		return new LongParameterListDetector()
	}

	def "finds one long parameter list with default threshold"() {
		expect:
		smells.size() == 1
		smells[0].name == "longMethod"
		!smells[0].parameters.isEmpty()
		smells[0].size == 6

		where:
		smells = run(Test.LONG_METHOD_DUMMY_PATH)
	}

	def "finds no long parameter list with specified threshold"() {
		expect:
		smells.size() == 0

		where:
		smells = run(Test.LONG_METHOD_DUMMY_PATH, new LongParameterListDetector(6))
	}
}
