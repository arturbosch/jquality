package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
final class MetricLoader {

	private final JarLoader jarLoader

	MetricLoader(JarLoader jarLoader) {
		this.jarLoader = jarLoader
	}

	List<MetricRaiser> loadSimple() {
		return jarLoader.load(MetricRaiser.class)
	}

	List<CompositeMetricRaiser> loadComposite() {
		return jarLoader.load(CompositeMetricRaiser.class)
	}
}
