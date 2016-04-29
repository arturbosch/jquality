package com.gitlab.artismarti.smartsmells.featureenvy

import com.gitlab.artismarti.smartsmells.domain.SourcePath
import com.gitlab.artismarti.smartsmells.domain.SourceRange
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author artur
 */
@Immutable
@ToString(includeNames = false, includePackage = false)
class FeatureEnvy {

	String methodName
	String methodSignature

	String object
	String objectSignature

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange
}
