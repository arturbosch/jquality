package io.gitlab.arturbosch.smartsmells.smells.cycle

import io.gitlab.arturbosch.smartsmells.common.Test

/**
 * @author artur
 */
class SingletonsAreNoCyclesTest extends AbstractCompilationTreeTest {

	def "singleton's are no cycles"() {
		expect:
		smells.size() == 0

		where:
		smells = new CycleDetector(Test.PATH).run(Test.SINGLETON_CYCLE_PATH)
	}
}
