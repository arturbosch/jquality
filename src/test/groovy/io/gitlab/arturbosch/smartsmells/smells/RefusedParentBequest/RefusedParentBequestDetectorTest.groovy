package io.gitlab.arturbosch.smartsmells.smells.RefusedParentBequest

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class RefusedParentBequestDetectorTest extends Specification {

	def "test"() {
		expect:
		smells.size() == 1

		where:
		smells = new RefusedParentBequestDetector().run(Test.BASE_PATH.resolve("refuse"))
	}

	def "test2"() {
		expect:
		smells.isEmpty()

		where:
		smells = new RefusedParentBequestDetector().run(Test.BASE_PATH.resolve("refusesame"))
	}
}
