package com.gitlab.artismarti.smartsmells.featureenvy

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class FeatureEnvyDetectorTest extends Specification {

	def "find one feature envy method not ignoring static methods"() {

		expect:
		smells.size() == 3
		smells.getAt(0).name == "envyMethod"
		smells.getAt(0).signature == "public void envyMethod()"
		smells.getAt(0).objectName == "otherLogic" || smells.getAt(1).objectName == "otherLogic"
		smells.getAt(0).objectName == "hasFeatures" || smells.getAt(1).objectName == "hasFeatures"
		smells.getAt(0).objectSignature == "HasLogic" || smells.getAt(1).objectSignature == "HasLogic"
		smells.getAt(0).objectSignature == "HasFeatures" || smells.getAt(1).objectSignature == "HasFeatures"
		smells.getAt(0).factor > 0.5d
		smells.getAt(0).factorThreshold == 0.52d

		where:
		smells = new FeatureEnvyDetector().run(Test.FEATURE_ENVY_PATH)
	}

	def "find one feature envy method ignoring static methods"() {

		expect:
		smells.size() == 2

		where:
		smells = new FeatureEnvyDetector(ignoreStatic: true).run(Test.FEATURE_ENVY_PATH)
	}

}
