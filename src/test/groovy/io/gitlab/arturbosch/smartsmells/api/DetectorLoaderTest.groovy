package io.gitlab.arturbosch.smartsmells.api

import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class DetectorLoaderTest extends Specification {

	def "loader extra detector from service entry"() {
		given: "empty facade and loader"
		def loader = new DetectorLoader(DetectorFacade.builder())
		when: "loading extra jar"
		def facade = loader.load([Paths.get(getClass().getResource("/detector.jar").path)])
		then: "one detector is found"
		facade.numberOfDetectors() == 1
	}
}
