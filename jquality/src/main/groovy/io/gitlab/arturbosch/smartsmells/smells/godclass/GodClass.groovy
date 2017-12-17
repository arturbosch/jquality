package io.gitlab.arturbosch.smartsmells.smells.godclass

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.ClassSpecific
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

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

	ElementTarget elementTarget = ElementTarget.CLASS

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}

	@Override
	String asCompactString() {
		"GodClass \n\nWMC: $weightedMethodPerClass with threshold: $weightedMethodPerClassThreshold" +
				"\nTCC: $tiedClassCohesion with threshold: $tiedClassCohesionThreshold" +
				"\nATFD: $accessToForeignData with threshold: $accessToForeignDataThreshold"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

	@Override
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz) {
		return new GodClass(clazz.nameAsString, ClassHelper.createFullSignature(clazz),
				weightedMethodPerClass, tiedClassCohesion, accessToForeignData,
				weightedMethodPerClassThreshold, tiedClassCohesionThreshold, accessToForeignDataThreshold,
				sourcePath, SourceRange.fromNode(clazz), elementTarget)
	}

	@Override
	String name() {
		return name
	}

	@Override
	String signature() {
		return signature
	}

	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false

		GodClass godClass = (GodClass) o

		if (accessToForeignData != godClass.accessToForeignData) return false
		if (accessToForeignDataThreshold != godClass.accessToForeignDataThreshold) return false
		if (Double.compare(godClass.tiedClassCohesion, tiedClassCohesion) != 0) return false
		if (Double.compare(godClass.tiedClassCohesionThreshold, tiedClassCohesionThreshold) != 0) return false
		if (weightedMethodPerClass != godClass.weightedMethodPerClass) return false
		if (weightedMethodPerClassThreshold != godClass.weightedMethodPerClassThreshold) return false
		if (elementTarget != godClass.elementTarget) return false
		if (name != godClass.name) return false
		if (signature != godClass.signature) return false
		if (sourcePath != godClass.sourcePath) return false
		if (sourceRange != godClass.sourceRange) return false

		return true
	}

	int hashCode() {
		int result
		long temp
		result = (name != null ? name.hashCode() : 0)
		result = 31 * result + (signature != null ? signature.hashCode() : 0)
		result = 31 * result + weightedMethodPerClass
		temp = tiedClassCohesion != +0.0d ? Double.doubleToLongBits(tiedClassCohesion) : 0L
		result = 31 * result + (int) (temp ^ (temp >>> 32))
		result = 31 * result + accessToForeignData
		result = 31 * result + weightedMethodPerClassThreshold
		temp = tiedClassCohesionThreshold != +0.0d ? Double.doubleToLongBits(tiedClassCohesionThreshold) : 0L
		result = 31 * result + (int) (temp ^ (temp >>> 32))
		result = 31 * result + accessToForeignDataThreshold
		result = 31 * result + (sourcePath != null ? sourcePath.hashCode() : 0)
		result = 31 * result + (sourceRange != null ? sourceRange.hashCode() : 0)
		result = 31 * result + (elementTarget != null ? elementTarget.hashCode() : 0)
		return result
	}
}
