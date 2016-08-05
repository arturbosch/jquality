package io.gitlab.arturbosch.smartsmells.smells.largeclass

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.smartsmells.common.Smelly
import io.gitlab.arturbosch.smartsmells.common.source.SourcePath
import io.gitlab.arturbosch.smartsmells.common.source.SourceRange

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false, includeNames = false)
class LargeClass implements Smelly {

	String name
	String signature

	int size
	int threshold

	@Delegate
	SourcePath sourcePath

	@Delegate
	SourceRange sourceRange

	@Override
	String asCompactString() {
		"LargeClass \n\nLOC: $size with threshold: $threshold"
	}
}
