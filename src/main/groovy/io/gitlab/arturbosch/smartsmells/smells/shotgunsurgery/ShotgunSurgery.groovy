package io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.ClassSpecific
import io.gitlab.arturbosch.smartsmells.smells.NameAndSignatureSpecific

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

	@Override
	String asCompactString() {
		"ShotgunSurgery \n\nCC=$cc with threshold: $ccThreshold" +
				"\nCM=$cm with threshold: $cmThreshold"
	}

	@Override
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz) {
		return new ShotgunSurgery(clazz.nameAsString, ClassHelper.createFullSignature(clazz),
				cc, cm, ccThreshold, cmThreshold,
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
