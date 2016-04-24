package com.gitlab.artismarti.smartsmells.featureenvy

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class FeatureEnvyDetectorTest extends Specification {

	def "find one feature envy method"() {
		expect:
		smells.size() == 1

		where:
		smells = new FeatureEnvyDetector().run(Test.PATH)
	}
}
