package io.gitlab.arturbosch.smartsmells.smells.longmethod

import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.MethodSpecific

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class LongMethod implements MethodSpecific {

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
		"LongMethod \n\nSize: $size with threshold: $threshold"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}\$$signature"
	}

	@Override
	MethodSpecific copy(MethodDeclaration method) {
		return new LongMethod(method.getNameAsString(), method.declarationAsString,
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
