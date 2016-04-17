package com.gitlab.artismarti.smartsmells.godclass

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class GodClassDetectorTest extends Specification {

	def "find one god class"() {
		expect:
		smells.size() == 1
		smells.get(0).accessToForeignData == 6
		smells.get(0).weightedMethodPerClass == 21
		smells.get(0).tiedClassCohesion == 0.0d

		where:
		smells = new GodClassDetector().run(Test.GOD_CLASS_DUMMY_PATH)
	}
}
