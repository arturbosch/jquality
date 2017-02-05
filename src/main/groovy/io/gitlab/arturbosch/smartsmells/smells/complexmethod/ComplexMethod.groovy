package io.gitlab.arturbosch.smartsmells.smells.complexmethod

import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.MethodSpecific
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethod

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class ComplexMethod implements MethodSpecific {

	String name
	String signature
	int size
	int threshold

	@Delegate
	SourceRange sourceRange
	@Delegate
	SourcePath sourcePath

	@Override
	String asCompactString() {
		"ComplexMethod \n\nCyclomaticComplexity: $size"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

	static of(LongMethod lm) {
		new ComplexMethod(lm.name, lm.signature, lm.size, lm.threshold, lm.sourceRange, lm.sourcePath)
	}

	@Override
	MethodSpecific copy(MethodDeclaration method) {
		return new ComplexMethod(method.getNameAsString(), method.declarationAsString,
				size, threshold, SourceRange.fromNode(method), sourcePath)
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
