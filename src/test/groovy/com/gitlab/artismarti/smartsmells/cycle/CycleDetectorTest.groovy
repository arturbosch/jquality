package com.gitlab.artismarti.smartsmells.cycle

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class CycleDetectorTest extends Specification {

	def "find one cycle"() {
		expect:
		smells.size() == 1

		where:
		smells = new CycleDetector().run(Test.PATH)
	}
}
