package io.gitlab.arturbosch.smartsmells.config

import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author artur
 */
class DetectorConfigTest extends Specification {

	def "loadValidConfig"() {
		expect:
		!config.getKey("godclass").isEmpty()
		!config.getKey("longmethod").isEmpty()

		where:
		path = Paths.get(getClass().getResource("/detector-config.yml").getFile())
		config = DetectorConfig.load(path)
	}

}
