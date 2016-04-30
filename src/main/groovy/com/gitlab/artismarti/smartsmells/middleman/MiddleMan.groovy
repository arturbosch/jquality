package com.gitlab.artismarti.smartsmells.middleman

import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author artur
 */
@Immutable
@ToString(includeNames = false, includePackage = false)
class MiddleMan {

	String name
	String signature

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange
}
