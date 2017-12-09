package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
@Immutable
@ToString(includePackage = false, includeNames = true)
class ClassInfo implements DetectionResult {

	String name
	String signature
	final List<Metric> metrics

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	ElementTarget elementTarget = ElementTarget.CLASS

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}

	@Override
	String asCompactString() {
		return toString()
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

	@Override
	String toString() {
		return "ClassInfo{name=$name, signature=$signature\n\t\t" +
				metrics.collect { it.toString() }.join("\n\t\t") +
				"}"
	}
}
