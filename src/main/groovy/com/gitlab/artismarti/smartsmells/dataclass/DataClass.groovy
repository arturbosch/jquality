package com.gitlab.artismarti.smartsmells.dataclass

import com.gitlab.artismarti.smartsmells.domain.SourcePath
import com.gitlab.artismarti.smartsmells.domain.SourceRange
import groovy.transform.Immutable
/**
 * @author artur
 */
@Immutable
class DataClass {

	String name
	String signature

	@Delegate
	SourceRange sourceRange
	@Delegate
	SourcePath sourcePath

}
