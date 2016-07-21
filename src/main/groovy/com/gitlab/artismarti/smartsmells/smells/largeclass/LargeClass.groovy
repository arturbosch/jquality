package com.gitlab.artismarti.smartsmells.smells.largeclass

import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import groovy.transform.Immutable
import groovy.transform.ToString

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
