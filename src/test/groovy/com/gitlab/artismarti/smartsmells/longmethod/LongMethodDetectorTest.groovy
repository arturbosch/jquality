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
