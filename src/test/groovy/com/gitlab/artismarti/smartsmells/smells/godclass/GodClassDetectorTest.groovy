package com.gitlab.artismarti.smartsmells.smells.godclass

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
		smells.getAt(0).accessToForeignData == 5
		smells.getAt(0).weightedMethodPerClass == 22
		smells.getAt(0).tiedClassCohesion == 0.7d
		smells.getAt(0).accessToForeignDataThreshold == 4
		smells.getAt(0).weightedMethodPerClassThreshold == 20
		smells.getAt(0).tiedClassCohesionThreshold == 0.8d
		smells.getAt(0).sourcePath != null
		smells.getAt(0).sourceRange != null

		where:
		smells = new GodClassDetector(tccThreshold: 0.8d).run(Test.GOD_CLASS_DUMMY_PATH)
	}
}
