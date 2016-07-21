package com.gitlab.artismarti.smartsmells.smells.longmethod

import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import groovy.transform.Immutable
import groovy.transform.ToString

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
