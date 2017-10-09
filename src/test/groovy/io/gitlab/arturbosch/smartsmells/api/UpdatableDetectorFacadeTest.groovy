package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.smartsmells.common.Test
import io.gitlab.arturbosch.smartsmells.config.Smell
import spock.lang.Specification

import java.nio.file.Files
import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
class UpdatableDetectorFacadeTest extends Specification {

	def "test running two times over same project"() {
		given:
		def fullStackFacade = DetectorFacade.builder().fullStackFacade()
		def facade = new UpdatableDetectorFacade(fullStackFacade, JPAL.updatable())
		when:
		facade.addOrUpdate(Collections.singletonList(Test.CYCLE_DUMMY_PATH))
		facade.addOrUpdate(Collections.singletonList(Test.COMPLEX_METHOD_DUMMY_PATH))
		def smellResult = facade.run()
		then:
		smellResult.of(Smell.CYCLE).size() == 1
		smellResult.of(Smell.COMPLEX_METHOD).size() == 1
	}

	def "updatable with filters has no smell result"() {
		given:
		def detectorFacade = DetectorFacade.builder().withFilters([".*/java/.*"]).fullStackFacade()
		def storage = JPAL.updatable(null, detectorFacade.filters)
		def facade = new UpdatableDetectorFacade(detectorFacade, storage)
		def paths = Files.walk(Test.BASE_PATH)
				.filter { Files.isRegularFile(it) }
				.collect(Collectors.toList())
		when:
		facade.addOrUpdate(paths)
		def smellResult = facade.run()
		then:
		smellResult.smellSets.entrySet().stream().flatMap { it.value.stream() }.collect(Collectors.toList()).size() == 0
	}
}
