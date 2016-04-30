package com.gitlab.artismarti.smartsmells.featureenvy

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class FeatureEnvyDetectorTest extends Specification {

	def "find one feature envy method"() {

		expect:
		smells.size() == 2
		smells.get(0).methodName == "envyMethod"
		smells.get(0).methodSignature == "public void envyMethod()"
		smells.get(0).object == "otherLogic"
		smells.get(0).objectSignature == "HasLogic"
		smells.get(0).factor > 0.5d
		smells.get(0).factorThreshold == 0.52d

		where:
		smells = new FeatureEnvyDetector().run(Test.FEATURE_ENVY_PATH)
	}
}
