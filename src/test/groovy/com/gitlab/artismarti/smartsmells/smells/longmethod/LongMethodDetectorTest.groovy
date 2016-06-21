package com.gitlab.artismarti.smartsmells.smells.longmethod

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class LongMethodDetectorTest extends Specification {

	def "find one long method with size 15 and threshold 14"() {
		expect:
		smells.size() == 1
		smells.getAt(0).name == "longMethod"
		smells.getAt(0).signature ==
				"void longMethod(String s1, String s2, String s3, String s4, String s5, String s6)"
		smells.getAt(0).size == 15
		smells.getAt(0).threshold == 14
		smells.getAt(0).header ==
				"public void longMethod(String s1, String s2, String s3, String s4, String s5, String s6)"

		where:
		smells = new LongMethodDetector(14).run(Test.LONG_METHOD_DUMMY_PATH)
	}

	def "find two long methods one is a long constructor with default threshold"() {
		expect:
		smells.size() == 2
		smells.getAt(0).name == "longMethod"

		where:
		smells = new LongMethodDetector().run(Test.LONG_METHOD_DUMMY_PATH)
	}

	def "find no long method with custom threshold"() {
		expect:
		smells.size() == 0

		where:
		smells = new LongMethodDetector(20).run(Test.LONG_METHOD_DUMMY_PATH)
	}
}
