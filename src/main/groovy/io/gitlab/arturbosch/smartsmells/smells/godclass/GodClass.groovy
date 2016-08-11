package io.gitlab.arturbosch.smartsmells.smells.godclass

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.Smelly

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
