package com.gitlab.artismarti.smartsmells.largeclass

import com.gitlab.artismarti.smartsmells.domain.SourcePath
import com.gitlab.artismarti.smartsmells.domain.SourceRange
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false, includeNames = false)
class LargeClass {

	String name
	String signature

	@Delegate
	SourcePath sourcePath

	@Delegate
	SourceRange sourceRange
}
