package io.gitlab.arturbosch.smartsmells.smells.largeclass

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class LargeClassDetectorTest extends Specification {

	def "find one large class"() {
		expect:
		smells.size() == 1
		smells[0].name == "LargeClassDummy"
		smells[0].signature == "LargeClassDummy"
		smells[0].sourcePath != null
		smells[0].sourceRange != null

		where:
		smells = new LargeClassDetector().run(Test.PATH)
	}
}
