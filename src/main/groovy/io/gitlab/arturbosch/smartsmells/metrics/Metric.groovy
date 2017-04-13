package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.util.Validate

/**
 * @author Artur Bosch
 */
@CompileStatic
class Metric {

	final String type
	final Integer value
	final boolean isDouble
	int threshold

	private Metric(final String type, final int value, final boolean isDouble, final int threshold) {
		this.type = type
		this.value = value
		this.isDouble = isDouble
		this.threshold = threshold
	}

	static Metric of(final String type, final double value, double threshold) {
		Validate.notNull(value)
		return new Metric(type, normalize(value), true, normalize(threshold))
	}

	static Metric of(final String type, final int value, int threshold) {
		Validate.notNull(type)
		Validate.notNull(value)
		return new Metric(type, value, false, threshold)
	}

	static int normalize(double factor) {
		return new Double(factor * 100).intValue()
	}

	@Override
	String toString() {
		return "Metric{" +
				"type=" + type +
				", value=" + value +
				'}'
	}
}