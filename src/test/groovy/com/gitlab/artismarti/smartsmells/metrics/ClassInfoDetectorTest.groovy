package com.gitlab.artismarti.smartsmells.metrics

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class ClassInfoDetectorTest extends Specification {

	def "three class info for feature envy dummy"() {

		expect: "dummy, two envies and static class"
		smells.size() == 4

		where:
		smells = new ClassInfoDetector().run(Test.FEATURE_ENVY_PATH)
	}
}
