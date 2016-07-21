package com.gitlab.artismarti.smartsmells.smells.dataclass

import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import groovy.transform.Immutable
/**
 * @author artur
 */
@Immutable
class DataClass implements Smelly {

	String name
	String signature

	@Delegate
	SourceRange sourceRange
	@Delegate
	SourcePath sourcePath

	@Override
	String asCompactString() {
		"DataClass \n\nContains only getter and setters, no logic."
	}
}
