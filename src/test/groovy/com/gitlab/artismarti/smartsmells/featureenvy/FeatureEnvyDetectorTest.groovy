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
		smells.getAt(0).name == "envyMethod"
		smells.getAt(0).signature == "public void envyMethod()"
		smells.getAt(0).object == "otherLogic"
		smells.getAt(0).objectSignature == "HasLogic"
		smells.getAt(0).factor > 0.5d
		smells.getAt(0).factorThreshold == 0.52d

		where:
		smells = new FeatureEnvyDetector().run(Test.FEATURE_ENVY_PATH)
	}
}
