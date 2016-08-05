package io.gitlab.arturbosch.smartsmells.config

import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author artur
 */
public class DetectorInitializerTest extends Specification {

	def "config has five detectors"() {
		expect:
		detectors.size() == 5

		where:
		path = Paths.get(getClass().getResource("/detector-config.yml").getFile())
		detectors = DetectorInitializer.init(DetectorConfig.load(path));
	}

	def "config has twelve detectors"() {
		expect:
		detectors.size() == 12

		where:
		path = Paths.get(getClass().getResource("/integration.yml").getFile())
		detectors = DetectorInitializer.init(DetectorConfig.load(path));
	}

}
