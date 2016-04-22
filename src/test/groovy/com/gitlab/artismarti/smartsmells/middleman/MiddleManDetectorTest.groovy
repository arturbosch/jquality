package com.gitlab.artismarti.smartsmells.middleman

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class MiddleManDetectorTest extends Specification {

	def "find one middle man class"() {
		expect:
		smells.size() == 1
		smells.get(0).name == "ManInTheMiddle"
		smells.get(0).signature == "ManInTheMiddle"
		smells.get(0).sourcePath != null
		smells.get(0).sourceRange != null

		where:
		smells = new MiddleManDetector().run(Test.MIDDLE_MAN_PATH)
	}
}
