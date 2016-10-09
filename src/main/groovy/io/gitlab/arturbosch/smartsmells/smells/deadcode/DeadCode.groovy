package io.gitlab.arturbosch.smartsmells.smells.deadcode

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.DetectionResult

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
	public String toString() {
		return "DeadCode{" +
				"entityName='" + name + '\'' +
				", signature='" + signature + '\'' +
				", path=" + sourcePath +
				", positions=" + sourceRange +
				'}';
	}

	@Override
	String asCompactString() {
		"Deadcode \n\nName: $name\nType: $entityType"
	}
}
