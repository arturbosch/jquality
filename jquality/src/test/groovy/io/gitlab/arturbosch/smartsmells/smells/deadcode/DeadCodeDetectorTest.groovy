package io.gitlab.arturbosch.smartsmells.smells.deadcode

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class DeadCodeDetectorTest extends Specification {

	def "find one unused field, method, parameter and locale variable"() {
		expect:
		smells.size() == 4
		smells[0].name.startsWith("dead")

		where:
		smells = new DeadCodeDetector().run(Test.DEAD_CODE_PATH)
	}

	def "find no dead code for variables which are used in conditions"() {
		expect:
		smells.size() == 0

		where:
		smells = new DeadCodeDetector().run(Test.DEAD_CODE_CONDITIONAL_PATH)
	}
}
