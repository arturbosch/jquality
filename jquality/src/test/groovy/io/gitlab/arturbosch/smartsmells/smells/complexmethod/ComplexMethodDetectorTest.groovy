package io.gitlab.arturbosch.smartsmells.smells.complexmethod

import io.gitlab.arturbosch.smartsmells.DetectorSpecification
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Test

/**
 * @author artur
 */
class ComplexMethodDetectorTest extends DetectorSpecification<ComplexMethod> {

	@Override
	Detector<ComplexMethod> detector() {
		return new ComplexMethodDetector()
	}

	def "finds one complex method"() {
		expect:
		smells.size() == 1
		smells[0].size == 11
		smells[0].name == "complexMethod"

		where:
		smells = run(Test.COMPLEX_METHOD_DUMMY_PATH)
	}
}
