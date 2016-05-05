package com.gitlab.artismarti.smartsmells.deadcode

import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class DeadCode implements Smelly {

	String entityName
	String signature

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange


	@Override
	public String toString() {
		return "DeadCode{" +
				"entityName='" + entityName + '\'' +
				", signature='" + signature + '\'' +
				", path=" + sourcePath +
				", positions=" + sourceRange +
				'}';
	}
}
