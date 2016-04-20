package com.gitlab.artismarti.smartsmells.largeclass

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class LargeClassDetectorTest extends Specification {

	def "find one large class"() {
		expect:
		smells.size() == 1
		smells.get(0).name == "LargeClassDummy"
		smells.get(0).signature == "LargeClassDummy"
		smells.get(0).sourcePath != null
		smells.get(0).sourceRange != null

		where:
		smells = new LargeClassDetector().run(Test.PATH)
	}
}
