package io.gitlab.arturbosch.smartsmells.smells.longmethod

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.Smelly

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
