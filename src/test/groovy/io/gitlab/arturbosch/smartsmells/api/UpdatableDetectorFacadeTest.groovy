package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.smartsmells.common.Test
import io.gitlab.arturbosch.smartsmells.config.Smell
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class UpdatableDetectorFacadeTest extends Specification {

	def "test running two times over same project"() {
		given:
		def fullStackFacade = DetectorFacade.builder().fullStackFacade()
		def facade = new UpdatableDetectorFacade(Test.BASE_PATH, fullStackFacade)
		when:
		facade.addOrUpdate(Collections.singletonList(Test.CYCLE_DUMMY_PATH))
		facade.addOrUpdate(Collections.singletonList(Test.COMPLEX_METHOD_DUMMY_PATH))
		def smellResult = facade.run()
		then:
		smellResult.of(Smell.CYCLE).size() == 1
		smellResult.of(Smell.COMPLEX_METHOD).size() == 1
	}
}
