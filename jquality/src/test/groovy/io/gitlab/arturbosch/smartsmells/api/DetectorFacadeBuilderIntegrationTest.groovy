package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.smartsmells.config.dsl.DetectorConfigDslRunner
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class DetectorFacadeBuilderIntegrationTest extends Specification {

	def "build facade from config with additional detector"() {
		when: "mixing groovy dsl config with custom jar loading"
		def paths = [Paths.get(getClass().getResource("/detector.jar").path)]
		def groovyFile = new File(getClass().getResource("/dsl/config.groovy").path)
		def configDsl = DetectorConfigDslRunner.execute(groovyFile)
		def facade = DetectorFacade.builder()
				.fromConfig(configDsl.build())
				.withLoader(new DetectorLoader(new JarLoader(paths)))
				.build()
		then: "two detector is found"
		facade.numberOfDetectors() == 2
	}
}
