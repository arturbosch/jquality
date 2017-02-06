package io.gitlab.arturbosch.smartsmells.smells.deadcode

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class DeadCode implements DetectionResult {

	String name
	String signature

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	ElementTarget elementTarget = ElementTarget.ANY

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}

	@Override
	String toString() {
		return "DeadCode{" +
				"entityName='" + name + '\'' +
				", signature='" + signature + '\'' +
				", path=" + sourcePath +
				", positions=" + sourceRange +
				'}'
	}

	@Override
	String asCompactString() {
		"Deadcode \n\nName: $name\nType: $elementTarget"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

}
