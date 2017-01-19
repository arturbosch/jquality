package io.gitlab.arturbosch.smartsmells.smells.nestedblockdepth

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class NestedBlockDepthDetectorTest extends Specification {

	def "find deep nested method of size 5"() {
		expect: "that both complex method dummy methods are too deep nested"
		smells.size() == 2
		smells.depth.each { assert it == 4 }
		smells.depthThreshold.each { assert it == 3 }
		where:
		smells = new NestedBlockDepthDetector().run(Test.COMPLEX_METHOD_DUMMY_PATH)
	}

}
