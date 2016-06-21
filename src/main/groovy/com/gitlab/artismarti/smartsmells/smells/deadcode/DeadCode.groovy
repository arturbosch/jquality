package com.gitlab.artismarti.smartsmells.smells.deadcode

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

	String name
	String signature
	String entityType

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange


	@Override
	public String toString() {
		return "DeadCode{" +
				"entityName='" + name + '\'' +
				", signature='" + signature + '\'' +
				", path=" + sourcePath +
				", positions=" + sourceRange +
				'}';
	}
}
