package io.gitlab.arturbosch.smartsmells.smells.classdatashouldbeprivate

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
@ToString(includePackage = false)
class ClassDataShouldBePrivate implements ClassSpecific {

	String name
	String signature

	@Delegate
	SourceRange sourceRange
	@Delegate
	SourcePath sourcePath

	ElementTarget elementTarget = ElementTarget.CLASS

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}

	@Override
	String asCompactString() {
		"ClassDataShouldBePrivate \n\nViolation of the principle of encapsulation."
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

	@Override
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz) {
		return new ClassDataShouldBePrivate(clazz.nameAsString, ClassHelper.createFullSignature(clazz),
				SourceRange.fromNode(clazz), sourcePath, elementTarget)
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
