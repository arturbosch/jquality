package io.gitlab.arturbosch.smartsmells.smells.largeclass

import io.gitlab.arturbosch.smartsmells.DetectorSpecification
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Test

/**
 * @author artur
 */
class LargeClassDetectorTest extends DetectorSpecification<LargeClass> {

	def "find one large class"() {
		expect:
		smells.size() == 1
		smells[0].name == "LargeClassDummy"
		smells[0].signature == "LargeClassDummy"
		smells[0].sourcePath != null
		smells[0].sourceRange != null

		where:
		smells = run(Test.BASE_PATH.resolve("LargeClassDummy.java"))
	}

	@Override
	Detector<LargeClass> detector() {
		return new LargeClassDetector()
	}
}
