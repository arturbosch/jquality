package io.gitlab.arturbosch.smartsmells.smells.complexcondition

import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.nodeTypes.NodeWithCondition
import com.github.javaparser.ast.stmt.Statement
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.internal.Printer
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
	Set<String> cases
	int threshold

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	ElementTarget elementTarget = ElementTarget.LOCAL

	@Override
	String name() {
		return inScope
	}

	@Override
	String signature() {
		return signature
	}

	@Override
	LocalSpecific copy(Statement statement) {
		if (statement instanceof NodeWithCondition) {
			def method = NodeHelper.findDeclaringMethod(statement).orElse(null)
			if (!method) return this
			def clazz = NodeHelper.findDeclaringClass(method).orElse(null)
			if (!clazz) return this
			def scope = ClassHelper.createFullSignature(clazz) + "#" + method.declarationAsString
			def expression = statement.condition
			def cases = new HashSet<String>()
			Conditions.whileBinaryExpression(cases, expression)
			def newSignature = expression.toString(Printer.NO_COMMENTS)
			return new ComplexCondition(scope, newSignature, cases, threshold,
					sourcePath, SourceRange.fromNode(statement), elementTarget)
		}
		return this
	}

	@Override
	LocalSpecific copy(Expression expression) {
		return this
	}

	@Override
	String asCompactString() {
		return "ComplexCondition \n\nSize: ${cases.size()} with threshold: $threshold"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}\$${inScope}\$$signature"
	}

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}

	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false

		ComplexCondition that = (ComplexCondition) o

		if (threshold != that.threshold) return false
		if (cases != that.cases) return false
		if (elementTarget != that.elementTarget) return false
		if (inScope != that.inScope) return false
		if (signature != that.signature) return false
		if (sourcePath != that.sourcePath) return false
		if (sourceRange != that.sourceRange) return false

		return true
	}

	int hashCode() {
		int result
		result = (inScope != null ? inScope.hashCode() : 0)
		result = 31 * result + (signature != null ? signature.hashCode() : 0)
		result = 31 * result + (cases != null ? cases.hashCode() : 0)
		result = 31 * result + threshold
		result = 31 * result + (sourcePath != null ? sourcePath.hashCode() : 0)
		result = 31 * result + (sourceRange != null ? sourceRange.hashCode() : 0)
		result = 31 * result + (elementTarget != null ? elementTarget.hashCode() : 0)
		return result
	}
}
