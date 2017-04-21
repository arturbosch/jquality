package io.gitlab.arturbosch.smartsmells.api

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
final class JarLoader {

	private final URLClassLoader loader

	JarLoader(final List<Path> paths) {
		def jars = paths.stream()
				.filter { it.toString().endsWith(".jar") }
				.map { it.toUri().toURL() }
				.toArray { new URL[it] }

		loader = new URLClassLoader(jars, getClass().getClassLoader())
	}

	def <T> List<T> load(Class<T> aClass) {
		return ServiceLoader.load(aClass, loader).toList()
	}

}
