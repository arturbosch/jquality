package io.gitlab.arturbosch.smartsmells.smells.godclass

import io.gitlab.arturbosch.smartsmells.DetectorSpecification
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Test

/**
 * @author artur
 */
class GodClassDetectorTest extends DetectorSpecification<GodClass> {

	def "find one god class"() {
		expect:
		smells.size() == 1
		smells[0].name == "GodClassDummy"
		smells[0].signature == "GodClassDummy"
		smells[0].accessToForeignData == 5
		smells[0].weightedMethodPerClass == 22
		smells[0].tiedClassCohesion == 0.7d
		smells[0].accessToForeignDataThreshold == 4
		smells[0].weightedMethodPerClassThreshold == 20
		smells[0].tiedClassCohesionThreshold == 0.8d
		smells[0].sourcePath != null
		smells[0].sourceRange != null

		where:
		smells = run(Test.GOD_CLASS_DUMMY_PATH)
	}

	@Override
	Detector<GodClass> detector() {
		return new GodClassDetector(new GodClassThreshold(20, 0.8d, 4))
	}
}
