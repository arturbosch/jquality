package io.gitlab.arturbosch.smartsmells.smells.longmethod

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
class LongMethod implements Smelly {

	String header
	String name
	String signature
	int size
	int threshold

	SourceRange sourceRange
	@Delegate
	SourcePath sourcePath

	@Override
	String asCompactString() {
		"LongMethod \n\nSize: $size with threshold: $threshold"
	}
}
