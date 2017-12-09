package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.util.Validate

/**
 * @author Artur Bosch
 */
@CompileStatic
class Metric {

	final String abbreviation
	final String type
	final int value
	final boolean isDouble

	private Metric(final String type, final int value, final boolean isDouble) {
		this.type = type
		this.value = value
		this.isDouble = isDouble
		this.abbreviation = type.replaceAll("[a-z]", "")
	}

	double asDouble() {
		if (isDouble) return value / 100
		else throw new IllegalStateException("This metric is not a double value, retrieving it as double is invalid!")
	}

	static Metric of(final String type, final double value) {
		Validate.notNull(value)
		return new Metric(type, normalize(value), true)
	}

	static Metric of(final String type, final int value) {
		Validate.notNull(type)
		Validate.notNull(value)
		return new Metric(type, value, false)
	}

	static int normalize(double factor) {
		return new Double(factor * 100).intValue()
	}

	@Override
	String toString() {
		return "$type=${valueAsString()}"
	}

	private String valueAsString() {
		return isDouble ? asDouble() : value
	}
}
