package io.gitlab.arturbosch.smartsmells.smells.longparam

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class LongParameterListDetectorTest extends Specification {

	def "finds one long parameter list with default threshold"() {
		expect:
		smells.size() == 1
		smells[0].name == "longMethod"
		!smells[0].parameters.isEmpty()
		smells[0].numberOfParams == 6

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
