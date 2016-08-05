package io.gitlab.arturbosch.smartsmells.metrics

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class ClassInfoDetectorTest extends Specification {

	def "four class info for feature envy dummy"() {

		expect: "dummy, two envies and static class"
		smells.size() == 4

		where:
		smells = new ClassInfoDetector().run(Test.FEATURE_ENVY_PATH)
	}
}
