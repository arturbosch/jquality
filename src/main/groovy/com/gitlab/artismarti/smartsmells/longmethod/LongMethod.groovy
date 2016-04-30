package com.gitlab.artismarti.smartsmells.longmethod

import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class LongMethod {

	String header
	String name
	String signature
	int size
	int threshold
	SourceRange sourceRange

	@Delegate
	SourcePath sourcePath

}
