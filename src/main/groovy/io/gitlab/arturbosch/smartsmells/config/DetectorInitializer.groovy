package io.gitlab.arturbosch.smartsmells.config

import io.gitlab.arturbosch.smartsmells.common.Detector

/**
 * @author artur
 */
class DetectorInitializer {

	private final ArrayList<Detector> detectors

	private DetectorInitializer() {
		this.detectors = new ArrayList<>()
	}

	static List<Detector> init(DetectorConfig config) {

		DetectorInitializer initializer = new DetectorInitializer()

		Smell.values().each {
			it.initialize(config)
					.ifPresent { initializer.detectors.add(it) }
		}

		return initializer.detectors
	}

}
