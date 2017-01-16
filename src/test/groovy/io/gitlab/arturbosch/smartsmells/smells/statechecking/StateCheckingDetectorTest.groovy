package io.gitlab.arturbosch.smartsmells.smells.statechecking

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class StateCheckingDetectorTest extends Specification {

	@Ignore
	def "find state checking methods"() {
		expect:
		smells.size() == 3
		where:
		smells = new StateCheckingDetector().run(Test.STATE_CHECKING_PATH)
	}
}
