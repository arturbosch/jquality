package io.gitlab.arturbosch.smartsmells.smells.featureenvy

import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class FeatureEnvyDetectorTest extends Specification {

	def "find one feature envy method not ignoring static methods"() {
		expect:
		smell.name == "envyMethod"
		smell.signature == "public void envyMethod()"
		smell.objectName == "hasFeatures"
		smell.objectSignature == "HasFeatures"
		smell.factor > 0.5d
		smell.factorThreshold >= 0.52d

		where:
		smell = new FeatureEnvyDetector().run(Test.FEATURE_ENVY_PATH)[0]
	}

	def "find one feature envy method ignoring static methods"() {
		expect:
		smells.size() == 1

		where:
		smells = new FeatureEnvyDetector(ignoreStatic: true).run(Test.FEATURE_ENVY_PATH)
	}

	def "no envy on this class"() {
		expect:
		smells.isEmpty()

		where:
		smells = new FeatureEnvyDetector().run(Test.BASE_PATH.resolve("bla/BaseEnvy.java"))
	}

	def "no envy on implemented and extended types"() {
		given:
		def storage = JPAL.newInstance(Test.BASE_PATH.resolve("bla"))
		def resolver = new Resolver(storage)
		when:
		def path = Test.BASE_PATH.resolve("bla/SelfEnvy.java")
		def smells = new FeatureEnvyDetector().execute(storage.getCompilationInfo(path).get(), resolver)
		then:
		smells.isEmpty()

	}

}
