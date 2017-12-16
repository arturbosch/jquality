package io.gitlab.arturbosch.smartsmells.smells.dataclass

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.CompileStatic
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
@ToString(includePackage = false)
@CompileStatic
class DataClass implements ClassSpecific {

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
		"DataClass \n\nContains only getter and setters, no logic."
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

	@Override
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz) {
		return new DataClass(clazz.nameAsString, ClassHelper.createFullSignature(clazz),
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
