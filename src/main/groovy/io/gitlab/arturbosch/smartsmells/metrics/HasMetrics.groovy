package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.PackageScope

/**
 * @author Artur Bosch
 */
trait HasMetrics {

	@PackageScope
	Map<String, Metric> metrics

	/**
	 * Finds a metric with given name.
	 * @param metricName
	 * @return the metric or null
	 */
	Metric getMetric(String metricName) {
		return metrics[metricName]
	}

	Collection<Metric> getMetrics() {
		return metrics.values()
	}
}
