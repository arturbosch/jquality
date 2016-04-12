package com.gitlab.artismarti.smartsmells.longmethod

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class LongMethodDetectorTest extends Specification {

	def "find one long method with default threshold"() {
		expect:
		smells.size() == 1
		smells.get(0).name == "longMethod"
		smells.get(0).signature == "void longMethod(String s1, String s2, String s3, String s4, String s5)"
		smells.get(0).size == 11
		smells.get(0).threshold == 7
		smells.get(0).header == "public void longMethod(String s1, String s2, String s3, String s4, String s5)"
		smells.get(0).sourceRange.toString() == "[[8, 21], [9, 9]]"

		where:
		smells = new LongMethodDetector().run(Test.PATH)
	}

	def "find no long method with custom threshold"() {
		expect:
		smells.size() == 0

		where:
		smells = new LongMethodDetector(20).run(Test.PATH)
	}
}
