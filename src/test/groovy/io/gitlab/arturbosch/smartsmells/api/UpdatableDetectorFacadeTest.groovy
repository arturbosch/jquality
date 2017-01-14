package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.smartsmells.common.Test
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
		def facade = new UpdatableDetectorFacade(Test.BASE_PATH, fullStackFacade)
		def files = Files.walk(Test.BASE_PATH)
				.filter { Files.isRegularFile(it) }
				.collect(Collectors.toList())
		def files2 = Files.walk(Test.CYCLE_DUMMY_PATH)
				.filter { Files.isRegularFile(it) }
				.collect(Collectors.toList())
		when:
		def infos = facade.addOrUpdate(files)
		def smellResult = facade.run(infos)
		infos = facade.addOrUpdate(files2)
		def smellResult2 = facade.run(infos)
		then:
		smellResult != smellResult2
	}
}
