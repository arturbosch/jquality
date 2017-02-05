package io.gitlab.arturbosch.smartsmells.smells.deadcode

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class DeadCode implements DetectionResult {

	String name
	String signature
	String entityType

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange


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
		"Deadcode \n\nName: $name\nType: $entityType"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}\$$signature"
	}

}
