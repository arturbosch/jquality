package com.gitlab.artismarti.smartsmells.smells.godclass

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
class GodClass implements Smelly {

	String name
	String signature

	int weightedMethodPerClass
	double tiedClassCohesion
	int accessToForeignData

	int weightedMethodPerClassThreshold
	double tiedClassCohesionThreshold
	int accessToForeignDataThreshold

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	@Override
	String asCompactString() {
		"GodClass \n\nWMC: $weightedMethodPerClass with threshold: $weightedMethodPerClassThreshold" +
				"\nTCC: $tiedClassCohesion with threshold: $tiedClassCohesionThreshold" +
				"\nATFD: $accessToForeignData with threshold: $accessToForeignDataThreshold"
	}

	@Override
	public String toString() {
		return "GodClass{" +
				"name=" + name +
				", signature=" + signature +
				", weightedMethodPerClass=" + weightedMethodPerClass +
				", tiedClassCohesion=" + tiedClassCohesion +
				", accessToForeignData=" + accessToForeignData +
				", path=" + path +
				", positions=" + sourceRange +
				'}';
	}
}
