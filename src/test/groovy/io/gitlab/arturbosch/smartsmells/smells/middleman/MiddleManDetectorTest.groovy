package io.gitlab.arturbosch.smartsmells.smells.middleman

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class MiddleManDetectorTest extends Specification {

	def "find one middle man class"() {
		expect:
		smells.size() == 1
		smells[0].name == "ManInTheMiddle"
		smells[0].signature == "MiddleManDummy\$ManInTheMiddle"
		smells[0].sourcePath != null
		smells[0].sourceRange != null

		where:
		smells = new MiddleManDetector(MiddleManVisitor.MMT.all).run(Test.MIDDLE_MAN_PATH)
	}
}
