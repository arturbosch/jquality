package io.gitlab.arturbosch.smartsmells.smells.cycle

import groovy.transform.ToString
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author artur
 */
@ToString(includeNames = false, includePackage = false)
class Cycle implements DetectionResult {

	Dependency source
	Dependency target

	ElementTarget elementTarget = ElementTarget.TWO_CLASSES

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}

	Cycle(Dependency source, Dependency target) {
		this.source = source
		this.target = target
	}

	boolean comparePath(String path) {
		return source.path == path || target.path == path
	}

	@Override
	String asCompactString() {
		"Cycle \n\nSource: $source.name\nTarget: $target.name"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$source.signature#$target.signature"
	}

	@Override
	boolean equals(Object obj) {
		if (obj instanceof Cycle) {
			return (source == obj.source && target == obj.target) ||
					(source == obj.target && target == obj.source)
		}
		return false
	}

	@Override
	String toString() {
		return "Cycle{" +
				"source=" + source.toString() +
				", target=" + target.toString() +
				'}'
	}

	@Override
	int hashCode() {
		return Objects.hash(source) + Objects.hash(target)
	}
}
