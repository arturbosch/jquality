package io.gitlab.arturbosch.smartsmells.smells.longparam

import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.smells.MethodSpecific

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class LongParameterList implements MethodSpecific {

	String name
	String signature
	int size
	int threshold

	List<String> parameters

	@Delegate
	SourceRange sourceRange
	@Delegate
	SourcePath sourcePath

	ElementTarget elementTarget = ElementTarget.METHOD

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}

	@Override
	String asCompactString() {
		"LongParameterList \n\nSize: $size with threshold: $threshold"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

	@Override
	MethodSpecific copy(MethodDeclaration method) {
		return new LongParameterList(method.getNameAsString(), method.declarationAsString,
				size, threshold, parameters, SourceRange.fromNode(method), sourcePath, elementTarget)
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
