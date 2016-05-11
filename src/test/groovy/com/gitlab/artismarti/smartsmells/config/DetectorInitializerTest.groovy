package com.gitlab.artismarti.smartsmells.config

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
}
