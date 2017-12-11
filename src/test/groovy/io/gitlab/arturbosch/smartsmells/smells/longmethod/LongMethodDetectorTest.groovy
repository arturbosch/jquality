package io.gitlab.arturbosch.smartsmells.smells.longmethod

import io.gitlab.arturbosch.smartsmells.DetectorSpecification
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Test

/**
 * @author artur
 */
class LongMethodDetectorTest extends DetectorSpecification<LongMethod> {

	def "a complex method is too long"() {
		expect:
		smells.size() == 2

		where:
		smells = run(Test.COMPLEX_METHOD_DUMMY_PATH, new LongMethodDetector(7))
	}

	def "find one long method with size 17 and threshold 14"() {
		expect:
		smells.size() == 1
		smells[0].name == "longMethod"
		smells[0].signature ==
				"public void longMethod(String s1, String s2, String s3, String s4, String s5, String s6)"
		smells[0].size == 17
		smells[0].threshold == 14

		where:
		smells = run(Test.LONG_METHOD_DUMMY_PATH, new LongMethodDetector(14))
	}

	def "find two long methods one is a long constructor with default threshold"() {
		expect:
		smells.size() == 2
		smells[0].name == "longMethod" || smells[1].name == "longMethod"

		where:
		smells = run(Test.LONG_METHOD_DUMMY_PATH, new LongMethodDetector(7))
	}

	def "find no long method with custom threshold"() {
		expect:
		smells.size() == 0

		where:
		smells = run(Test.LONG_METHOD_DUMMY_PATH, new LongMethodDetector(20))
	}

	@Override
	Detector<LongMethod> detector() {
		return new LongMethodDetector()
	}
}
