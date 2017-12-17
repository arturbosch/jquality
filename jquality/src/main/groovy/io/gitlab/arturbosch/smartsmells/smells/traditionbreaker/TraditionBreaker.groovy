package io.gitlab.arturbosch.smartsmells.smells.traditionbreaker

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
@CompileStatic
@Immutable
@ToString(includePackage = false)
class TraditionBreaker implements ClassSpecific {

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
		"TraditionBreaker \n\nA class that doesn't use the protected members of its parent."
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
	}

	@Override
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz) {
		return new TraditionBreaker(clazz.nameAsString, ClassHelper.createFullSignature(clazz),
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

	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false

		TraditionBreaker that = (TraditionBreaker) o

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
		result = 31 * result + (sourceRange != null ? sourceRange.hashCode() : 0)
		result = 31 * result + (sourcePath != null ? sourcePath.hashCode() : 0)
		result = 31 * result + (elementTarget != null ? elementTarget.hashCode() : 0)
		return result
	}
}
