package io.gitlab.arturbosch.smartsmells.smells.largeclass

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ClassSpecific

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false, includeNames = false)
class LargeClass implements DetectionResult, ClassSpecific {

	String name
	String signature

	int size
	int threshold

	@Delegate
	SourcePath sourcePath

	@Delegate
	SourceRange sourceRange

	@Override
	String asCompactString() {
		"LargeClass \n\nLOC: $size with threshold: $threshold"
	}

	@Override
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz) {
		return new LargeClass(clazz.nameAsString, ClassHelper.createFullSignature(clazz),
				size, threshold, sourcePath, SourceRange.fromNode(clazz))
	}

}
