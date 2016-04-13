package com.gitlab.artismarti.smartsmells.dataclass

import com.gitlab.artismarti.smartsmells.domain.SourcePath
import groovy.transform.Immutable

/**
 * @author artur
 */
@Immutable
class DataClass {

	String name
	SourcePath sourcePath
}
