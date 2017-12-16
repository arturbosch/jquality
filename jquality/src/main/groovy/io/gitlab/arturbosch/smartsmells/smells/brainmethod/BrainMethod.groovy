package io.gitlab.arturbosch.smartsmells.smells.brainmethod

import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.metrics.MethodInfo
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.smells.MethodSpecific

/**
 * @author Artur Bosch
 */
@CompileStatic
@Immutable
@ToString(includePackage = false)
class BrainMethod implements MethodSpecific {

	String name
	String signature

	@Delegate
	SourceRange sourceRange
	@Delegate
	SourcePath sourcePath

	ElementTarget elementTarget = ElementTarget.METHOD

	static fromInfo(MethodInfo info) {
		return new BrainMethod(info.name, info.signature, info.sourceRange, info.sourcePath, ElementTarget.METHOD)
	}

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}

	@Override
	String asCompactString() {
		"BrainMethod"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

	@Override
	MethodSpecific copy(MethodDeclaration method) {
		return new BrainMethod(method.getNameAsString(), method.declarationAsString,
				SourceRange.fromNode(method), sourcePath, elementTarget)
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
