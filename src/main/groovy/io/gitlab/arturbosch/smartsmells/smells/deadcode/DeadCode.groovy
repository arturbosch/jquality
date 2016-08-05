package io.gitlab.arturbosch.smartsmells.smells.deadcode

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.smartsmells.common.Smelly
import io.gitlab.arturbosch.smartsmells.common.source.SourcePath
import io.gitlab.arturbosch.smartsmells.common.source.SourceRange

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class DeadCode implements Smelly {

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
