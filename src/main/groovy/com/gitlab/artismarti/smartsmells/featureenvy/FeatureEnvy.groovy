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

	@Override
	boolean equals(o) {
		if (this.is(o)) return true
		if (!(o instanceof FeatureEnvy)) return false

		FeatureEnvy that = (FeatureEnvy) o

		if (name != that.name) return false
		if (object != that.object) return false
		if (objectSignature != that.objectSignature) return false
		if (signature != that.signature) return false
		if (sourcePath != that.sourcePath) return false
		if (sourceRange != that.sourceRange) return false

		return true
	}

	@Override
	int hashCode() {
		int result
		result = (name != null ? name.hashCode() : 0)
		result = 31 * result + (signature != null ? signature.hashCode() : 0)
		result = 31 * result + (object != null ? object.hashCode() : 0)
		result = 31 * result + (objectSignature != null ? objectSignature.hashCode() : 0)
		result = 31 * result + (sourcePath != null ? sourcePath.hashCode() : 0)
		result = 31 * result + (sourceRange != null ? sourceRange.hashCode() : 0)
		return result
	}
}
