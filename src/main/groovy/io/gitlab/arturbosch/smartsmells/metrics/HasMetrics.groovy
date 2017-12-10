package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.PackageScope
import io.gitlab.arturbosch.smartsmells.util.Validate

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

	void addMetric(Metric metric) {
		Validate.notNull(metric)
		metrics.put(metric.type, metric)
	}
}
