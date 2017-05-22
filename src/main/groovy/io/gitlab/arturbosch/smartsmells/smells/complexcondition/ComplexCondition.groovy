package io.gitlab.arturbosch.smartsmells.smells.complexcondition

import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.DoStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.stmt.WhileStmt
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
		if (statement instanceof IfStmt || statement instanceof WhileStmt || statement instanceof DoStmt) {
			def method = NodeHelper.findDeclaringMethod(statement).orElse(null)
			if (!method) return this
			def clazz = NodeHelper.findDeclaringClass(method).orElse(null)
			if (!clazz) return this
			def scope = ClassHelper.createFullSignature(clazz) + "#" + method.declarationAsString
			def field = statement.getClass().getField("condition")
			def expression = field.get(statement) as Expression
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
}
