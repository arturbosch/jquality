package io.gitlab.arturbosch.smartsmells.smells.cycle

import com.github.javaparser.ast.body.FieldDeclaration
import groovy.transform.CompileStatic
import groovy.transform.Immutable
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.smells.FieldSpecific

/**
 * @author Artur Bosch
 */
@Immutable
@CompileStatic
class Dependency implements DetectionResult, FieldSpecific {

	String name
	String signature

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	static Dependency of(String entityName, String entitySignature, SourcePath sourcePath, SourceRange sourceRange) {
		return new Dependency(entityName, entitySignature, sourcePath, sourceRange)
	}

	@Override
	String toString() {
		return "{$name, $signature, $sourcePath, $sourceRange}"
	}

	@Override
	String asCompactString() {
		"Dependency \n\nName: $name"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}\$$signature"
	}

	@Override
	ElementTarget elementTarget() {
		return ElementTarget.FIELD
	}

	@Override
	String name() {
		return name
	}

	@Override
	String signature() {
		return signature
	}

	@Override
	FieldSpecific copy(FieldDeclaration field) {
		def fieldType = field.commonType.elementType.toString(Printer.NO_COMMENTS)
		def fieldSignature = field.toString(Printer.NO_COMMENTS)
		return new Dependency(fieldType, fieldSignature, sourcePath, SourceRange.fromNode(field))
	}

	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false

		Dependency that = (Dependency) o

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
		result = 31 * result + (sourcePath != null ? sourcePath.hashCode() : 0)
		result = 31 * result + (sourceRange != null ? sourceRange.hashCode() : 0)
		return result
	}
}
