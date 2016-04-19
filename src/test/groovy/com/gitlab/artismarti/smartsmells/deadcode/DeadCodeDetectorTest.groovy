package com.gitlab.artismarti.smartsmells.deadcode

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class DeadCodeDetectorTest extends Specification {

	def "find one unused field, method, parameter and locale variable"() {
		expect:
		smells.size() == 4
		smells.get(0).entityName == "deadMethod"
		println smells
		where:
		smells = new DeadCodeDetector().run(Test.DEAD_CODE_PATH)
	}
}
