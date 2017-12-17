package io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.ClassSpecific
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
@Immutable
@ToString(includePackage = false, excludes = ["ccThreshold", "cmThreshold"])
class ShotgunSurgery implements ClassSpecific {

	String name
	String signature
	int cc
	int cm
	int ccThreshold
	int cmThreshold
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
		"ShotgunSurgery \n\nCC=$cc with threshold: $ccThreshold" +
				"\nCM=$cm with threshold: $cmThreshold"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

	@Override
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz) {
		return new ShotgunSurgery(clazz.nameAsString, ClassHelper.createFullSignature(clazz),
				cc, cm, ccThreshold, cmThreshold, sourcePath, SourceRange.fromNode(clazz), ElementTarget.CLASS)
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

		ShotgunSurgery that = (ShotgunSurgery) o

		if (cc != that.cc) return false
		if (ccThreshold != that.ccThreshold) return false
		if (cm != that.cm) return false
		if (cmThreshold != that.cmThreshold) return false
		if (elementTarget != that.elementTarget) return false
		if (name != that.name) return false
		if (signature != that.signature) return false
		if (sourcePath != that.sourcePath) return false
		if (sourceRange != that.sourceRange) return false

		return true
	}

	int hashCode() {
		int result
		result = (name != null ? name.hashCode() : 0)
		result = 31 * result + (signature != null ? signature.hashCode() : 0)
		result = 31 * result + cc
		result = 31 * result + cm
		result = 31 * result + ccThreshold
		result = 31 * result + cmThreshold
		result = 31 * result + (sourcePath != null ? sourcePath.hashCode() : 0)
		result = 31 * result + (sourceRange != null ? sourceRange.hashCode() : 0)
		result = 31 * result + (elementTarget != null ? elementTarget.hashCode() : 0)
		return result
	}
}
