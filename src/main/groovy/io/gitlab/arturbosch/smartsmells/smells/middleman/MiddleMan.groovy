package io.gitlab.arturbosch.smartsmells.smells.middleman

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ClassSpecific

/**
 * @author artur
 */
@Immutable
@ToString(includeNames = false, includePackage = false)
class MiddleMan implements DetectionResult, ClassSpecific {

	String name
	String signature

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	@Override
	String asCompactString() {
		"MiddleMan \n\nMethods only delegate to others. No real logic."
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

	@Override
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz) {
		return new MiddleMan(clazz.nameAsString, ClassHelper.createFullSignature(clazz),
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
