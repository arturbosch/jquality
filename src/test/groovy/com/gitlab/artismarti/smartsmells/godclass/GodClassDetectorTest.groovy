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
		smells.get(0).name == "GodClassDummy"
		smells.get(0).signature == "GodClassDummy"
		smells.get(0).accessToForeignData == 6
		smells.get(0).weightedMethodPerClass == 21
		smells.get(0).tiedClassCohesion == 0.3d
		smells.get(0).accessToForeignDataThreshold == 4
		smells.get(0).weightedMethodPerClassThreshold == 20
		smells.get(0).tiedClassCohesionThreshold == 0.33d
		smells.get(0).sourcePath != null
		smells.get(0).sourceRange != null

		where:
		smells = new GodClassDetector().run(Test.GOD_CLASS_DUMMY_PATH)
	}
}
