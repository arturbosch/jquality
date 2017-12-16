package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
final class DetectorLoader {

	private final JarLoader jarLoader

	DetectorLoader(JarLoader jarLoader) {
		this.jarLoader = jarLoader
	}

	List<Detector> load() {
		return jarLoader.load(Detector.class)
	}

}
