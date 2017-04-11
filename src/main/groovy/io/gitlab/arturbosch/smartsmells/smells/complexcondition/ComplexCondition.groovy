package io.gitlab.arturbosch.smartsmells.smells.complexcondition

import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.Statement
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.smells.LocalSpecific

/**
 * @author Artur Bosch
 */
@Immutable
@ToString(includePackage = false)
class ComplexCondition implements DetectionResult, LocalSpecific {

	String inScope //ClassSignature#MethodSignature
	String signature

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	ElementTarget elementTarget = ElementTarget.LOCAL

	@Override
	String name() {
		return name
	}

	@Override
	String signature() {
		return signature
	}

	@Override
	LocalSpecific copy(Statement statement) {
		return null
	}

	@Override
	LocalSpecific copy(Expression expression) {
		return null
	}

	@Override
	String asCompactString() {
		return null
	}

	@Override
	String asComparableString() {
		return null
	}

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}
}
