package com.gitlab.artismarti.smartsmells.smells.largeclass

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class LargeClassDetectorTest extends Specification {

	def "find one large class"() {
		expect:
		smells.size() == 1
		smells.getAt(0).name == "LargeClassDummy"
		smells.getAt(0).signature == "LargeClassDummy"
		smells.getAt(0).sourcePath != null
		smells.getAt(0).sourceRange != null

		where:
		smells = new LargeClassDetector().run(Test.PATH)
	}
}
