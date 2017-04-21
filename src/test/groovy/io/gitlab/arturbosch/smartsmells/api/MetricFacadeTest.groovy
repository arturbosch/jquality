package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class MetricFacadeTest extends Specification {

	def "run metric facade on dummies"() {
		given: "metric facade"
		def facade = MetricFacade.builder().fullStackFacade()
		when: "running facade with metric processor"
		def result = facade.run(Test.BASE_PATH)
		then:
		!result.isEmpty()
		result.find { it.name == "CommentDummy" }
		result.find { it.name == "StateCheckingDummy" }
		result.find { it.name == "StateCheck1" }
		result.find { it.name == "FeatureEnvyDummy" }
		result.find { it.name == "Staticly" }
		result.find { it.name == "HasFeatures" }

		when: "averaging over all class infos"
		def averages = MetricFacade.average(result)
		then: "all must be double values"
		averages.stream().allMatch { it.isDouble }
	}

}
