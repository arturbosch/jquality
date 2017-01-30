package io.gitlab.arturbosch.smartsmells.smells.dataclass

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
@ToString(includePackage = false)
class DataClass implements DetectionResult, ClassSpecific {

	String name
	String signature

	@Delegate
	SourceRange sourceRange
	@Delegate
	SourcePath sourcePath

	@Override
	String asCompactString() {
		"DataClass \n\nContains only getter and setters, no logic."
	}

	@Override
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz) {
		return new DataClass(clazz.nameAsString, ClassHelper.createFullSignature(clazz),
				SourceRange.fromNode(clazz), sourcePath)
	}

}
