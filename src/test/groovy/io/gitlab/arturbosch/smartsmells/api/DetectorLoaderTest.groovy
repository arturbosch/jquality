package io.gitlab.arturbosch.smartsmells.api

import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class DetectorLoaderTest extends Specification {

	def "loader extra detector from service entry"() {
		when: "loading extra jar"
		def paths = [Paths.get(getClass().getResource("/detector.jar").path)]
		def facade = DetectorFacade.builder().withLoader(new DetectorLoader(paths)).build()
		then: "one detector is found"
		facade.numberOfDetectors() == 1
	}
}
