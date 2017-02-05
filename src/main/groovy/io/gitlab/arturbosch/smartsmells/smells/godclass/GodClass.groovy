package io.gitlab.arturbosch.smartsmells.smells.godclass

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.ClassSpecific

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false, excludes = ["weightedMethodPerClassThreshold", "tiedClassCohesionThreshold", "accessToForeignDataThreshold"])
class GodClass implements ClassSpecific {

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
	String asComparableString() {
		return "${javaClassName()}\$$signature"
	}

	@Override
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz) {
		return new GodClass(clazz.nameAsString, ClassHelper.createFullSignature(clazz),
				weightedMethodPerClass, tiedClassCohesion, accessToForeignData,
				weightedMethodPerClassThreshold, tiedClassCohesionThreshold, accessToForeignDataThreshold,
				sourcePath, SourceRange.fromNode(clazz))
	}

	@Override
	String name() {
		return name
	}

	@Override
	String signature() {
		return signature
	}

}
