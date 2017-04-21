package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.CompileStatic

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@CompileStatic
final class DetectorLoader {

	private List<Path> paths

	DetectorLoader(List<Path> paths) {
		this.paths = paths
	}

	List<Detector> load() {
		def jars = paths.stream()
				.filter { it.toString().endsWith(".jar") }
				.map { it.toUri().toURL() }
				.toArray { new URL[it] }

		def loader = new URLClassLoader(jars, getClass().getClassLoader())
		return ServiceLoader.load(Detector.class, loader).toList()
	}
}
