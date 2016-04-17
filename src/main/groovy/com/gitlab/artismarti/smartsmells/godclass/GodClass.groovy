package com.gitlab.artismarti.smartsmells.godclass

import com.gitlab.artismarti.smartsmells.domain.SourcePath
import com.gitlab.artismarti.smartsmells.domain.SourceRange
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class GodClass {

	int weightedMethodPerClass
	double tiedClassCohesion
	int accessToForeignData

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange


	@Override
	public String toString() {
		return "GodClass{" +
				"weightedMethodPerClass=" + weightedMethodPerClass +
				", tiedClassCohesion=" + tiedClassCohesion +
				", accessToForeignData=" + accessToForeignData +
				", path=" + path +
				", positions=" + sourceRange +
				'}';
	}
}
