package com.gitlab.artismarti.smartsmells.cycle

import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import groovy.transform.Immutable
/**
 * @author artur
 */
@Immutable
class Dependency implements Smelly {

	String name
	String signature

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	static Dependency of(String entityName, String entitySignature, SourcePath sourcePath, SourceRange sourceRange) {
		return new Dependency(entityName, entitySignature, sourcePath, sourceRange)
	}

	@Override
	public String toString() {
		return "{$name, $signature, $sourcePath, $sourceRange}"
	}

}
