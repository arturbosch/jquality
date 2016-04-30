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
		smells.getAt(0).name == "GodClassDummy"
		smells.getAt(0).signature == "GodClassDummy"
		smells.getAt(0).accessToForeignData == 6
		smells.getAt(0).weightedMethodPerClass == 21
		smells.getAt(0).tiedClassCohesion == 0.3d
		smells.getAt(0).accessToForeignDataThreshold == 4
		smells.getAt(0).weightedMethodPerClassThreshold == 20
		smells.getAt(0).tiedClassCohesionThreshold == 0.33d
		smells.getAt(0).sourcePath != null
		smells.getAt(0).sourceRange != null

		where:
		smells = new GodClassDetector().run(Test.GOD_CLASS_DUMMY_PATH)
	}
}
