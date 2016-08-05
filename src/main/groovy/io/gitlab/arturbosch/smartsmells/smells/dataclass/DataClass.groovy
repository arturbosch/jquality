package io.gitlab.arturbosch.smartsmells.smells.dataclass

import groovy.transform.Immutable
import io.gitlab.arturbosch.smartsmells.common.Smelly
import io.gitlab.arturbosch.smartsmells.common.source.SourcePath
import io.gitlab.arturbosch.smartsmells.common.source.SourceRange

/**
 * @author artur
 */
@Immutable
class DataClass implements Smelly {

	String name
	String signature

	@Delegate
	SourceRange sourceRange
	@Delegate
	SourcePath sourcePath

	@Override
	String asCompactString() {
		"DataClass \n\nContains only getter and setters, no logic."
	}
}
