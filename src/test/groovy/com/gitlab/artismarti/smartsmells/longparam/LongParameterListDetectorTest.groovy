package com.gitlab.artismarti.smartsmells.longparam

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class LongParameterListDetectorTest extends Specification {

	def "finds one long parameter list with default threshold"() {
		expect:
		smells.size() == 1
		smells.get(0).name == "longMethod"
		smells.get(0).parameters.size() == 6

		where:
		smells = new LongParameterListDetector().run(Test.PATH)
	}

	def "finds no long parameter list with specified threshold"() {
		expect:
		smells.size() == 0

		where:
		smells = new LongParameterListDetector(6).run(Test.PATH)
	}
}
