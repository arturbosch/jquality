package io.gitlab.arturbosch.smartsmells.smells.nestedblockdepth

import io.gitlab.arturbosch.smartsmells.DetectorSpecification
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Test

/**
 * @author Artur Bosch
 */
class NestedBlockDepthDetectorTest extends DetectorSpecification<NestedBlockDepth> {

	def "find deep nested method of size 5"() {
		expect: "that both complex method dummy methods are too deep nested"
		smells.size() == 2
		smells.depth.each { assert it == 4 }
		smells.depthThreshold.each { assert it == 3 }
		where:
		smells = run(Test.COMPLEX_METHOD_DUMMY_PATH)
	}

	@Override
	Detector<NestedBlockDepth> detector() {
		return new NestedBlockDepthDetector()
	}
}
