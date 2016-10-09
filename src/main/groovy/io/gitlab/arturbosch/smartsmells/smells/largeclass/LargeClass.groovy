package io.gitlab.arturbosch.smartsmells.smells.largeclass

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.DetectionResult

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false, includeNames = false)
class LargeClass implements DetectionResult {

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
