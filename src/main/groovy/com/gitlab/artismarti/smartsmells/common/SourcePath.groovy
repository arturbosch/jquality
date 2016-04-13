package com.gitlab.artismarti.smartsmells.common

import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false, includeNames = false)
class SourcePath {

	String path

	static def of(String path) {
		new SourcePath(path)
	}
}
