package com.gitlab.artismarti.smartsmells.deadcode

import com.gitlab.artismarti.smartsmells.domain.SourcePath
import com.gitlab.artismarti.smartsmells.domain.SourceRange
import groovy.transform.Immutable

/**
 * @author artur
 */
@Immutable
class DeadCode {

	String entityName
	String signature

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange
}
