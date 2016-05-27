package com.gitlab.artismarti.smartsmells.featureenvy

import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author artur
 */
@Immutable
@ToString(includeNames = false, includePackage = false)
class FeatureEnvy implements Smelly {

	String name
	String signature

	String object
	String objectSignature

	double factor
	double factorThreshold

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange
}
