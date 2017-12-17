package io.gitlab.arturbosch.smartsmells.smells.longmethod

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
class LongMethod implements MethodSpecific {

	String name
	String signature
	int size
	int threshold

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
		"LongMethod \n\nSize: $size with threshold: $threshold"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

	@Override
	MethodSpecific copy(MethodDeclaration method) {
		return new LongMethod(method.getNameAsString(), method.declarationAsString,
				size, threshold, SourceRange.fromNode(method), sourcePath, elementTarget)
	}

	@Override
	String name() {
		return name
	}

	@Override
	String signature() {
		return signature
	}

	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false

		LongMethod that = (LongMethod) o

		if (size != that.size) return false
		if (threshold != that.threshold) return false
		if (elementTarget != that.elementTarget) return false
		if (name != that.name) return false
		if (signature != that.signature) return false
		if (sourcePath != that.sourcePath) return false
		if (sourceRange != that.sourceRange) return false

		return true
	}

	int hashCode() {
		int result
		result = (name != null ? name.hashCode() : 0)
		result = 31 * result + (signature != null ? signature.hashCode() : 0)
		result = 31 * result + size
		result = 31 * result + threshold
		result = 31 * result + (sourceRange != null ? sourceRange.hashCode() : 0)
		result = 31 * result + (sourcePath != null ? sourcePath.hashCode() : 0)
		result = 31 * result + (elementTarget != null ? elementTarget.hashCode() : 0)
		return result
	}
}
