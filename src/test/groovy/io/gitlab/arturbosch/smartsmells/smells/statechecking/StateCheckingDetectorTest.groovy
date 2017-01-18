package io.gitlab.arturbosch.smartsmells.smells.statechecking

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class StateCheckingDetectorTest extends Specification {

	def "find state checking methods"() {
		expect:
		smells.size() == 4
		where:
		smells = new StateCheckingDetector().run(Test.STATE_CHECKING_PATH)
	}
}
