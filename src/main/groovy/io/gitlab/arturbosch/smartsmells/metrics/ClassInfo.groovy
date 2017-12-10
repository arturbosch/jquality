package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.Canonical
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.QualifiedType

/**
 * @author Artur Bosch
 */
@Canonical
@ToString(includePackage = false, includeNames = true)
class ClassInfo {

	final QualifiedType qualifiedType
	final String signature
	private final Map<String, Metric> metrics

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	ClassInfo(QualifiedType qualifiedType,
			  String signature,
			  Map<String, Metric> metrics,
			  SourcePath sourcePath,
			  SourceRange sourceRange) {
		this.qualifiedType = qualifiedType
		this.signature = signature
		this.metrics = metrics
		this.sourcePath = sourcePath
		this.sourceRange = sourceRange
	}

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

	/**
	 * @return just the simple class name, use qualifiedType.name for full name
	 */
	String getName() {
		return qualifiedType.shortName()
	}

	@Override
	String toString() {
		return "ClassInfo{name=$qualifiedType.name, signature=$signature\n\t\t" +
				metrics.collect { it.toString() }.join("\n\t\t") +
				"}"
	}
}
