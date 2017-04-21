package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.CompileStatic

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@CompileStatic
final class DetectorLoader {

	private DetectorFacadeBuilder facadeBuilder

	DetectorLoader(DetectorFacadeBuilder facadeBuilder) {
		this.facadeBuilder = facadeBuilder
	}

	DetectorFacade load(List<Path> paths) {
		def jars = paths.stream()
				.filter { it.toString().endsWith(".jar") }
				.map { it.toUri().toURL() }
				.toArray { new URL[it] }

		def loader = new URLClassLoader(jars, getClass().getClassLoader())
		ServiceLoader.load(Detector.class, loader).each {
			facadeBuilder.with(it)
		}

		return facadeBuilder.build()
	}
}
