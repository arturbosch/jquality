package io.gitlab.arturbosch.smartsmells.smells.cycle

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class SingletonsAreNoCyclesTest extends Specification {

	def "singleton's are no cycles"() {
		expect:
		smells.size() == 0

		where:
		smells = new CycleDetector().run(Test.SINGLETON_CYCLE_PATH)
	}
}
