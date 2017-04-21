package io.gitlab.arturbosch.smartsmells

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade
import io.gitlab.arturbosch.smartsmells.api.DetectorFacadeBuilder

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
