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
		smells.getAt(0).name == "ManInTheMiddle"
		smells.getAt(0).signature == "MiddleManDummy\$ManInTheMiddle"
		smells.getAt(0).sourcePath != null
		smells.getAt(0).sourceRange != null

		where:
		smells = new MiddleManDetector(MiddleManVisitor.MMT.all).run(Test.MIDDLE_MAN_PATH)
	}
}
