package com.gitlab.artismarti.smartsmells.cycle

import com.gitlab.artismarti.smartsmells.common.Test

/**
 * @author artur
 */
class SingletonsAreNoCyclesTest extends AbstractCycleTest {

	def "singleton's are no cycles"() {
		expect:
		smells.size() == 0

		where:
		smells = new CycleDetector(Test.PATH).run(Test.SINGLETON_CYCLE_PATH)
	}
}
