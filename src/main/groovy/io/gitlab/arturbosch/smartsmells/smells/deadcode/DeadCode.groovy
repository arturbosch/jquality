package io.gitlab.arturbosch.smartsmells.smells.deadcode

import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.stmt.Statement
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.smartsmells.smells.ClassSpecific
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.smells.FieldSpecific
import io.gitlab.arturbosch.smartsmells.smells.LocalSpecific
import io.gitlab.arturbosch.smartsmells.smells.MethodSpecific
import io.gitlab.arturbosch.smartsmells.util.Strings

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class DeadCode implements DetectionResult, MethodSpecific, ClassSpecific, FieldSpecific, LocalSpecific {

	String name
	String signature

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	ElementTarget elementTarget = ElementTarget.ANY

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}

	@Override
	String toString() {
		return "DeadCode{" +
				"entityName='" + name + '\'' +
				", signature='" + signature + '\'' +
				", path=" + sourcePath +
				", positions=" + sourceRange +
				'}'
	}

	@Override
	String asCompactString() {
		"Deadcode \n\nName: $name\nType: $elementTarget"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$signature"
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
	MethodSpecific copy(MethodDeclaration method) {
		return new DeadCode(method.nameAsString, method.declarationAsString,
				sourcePath, SourceRange.fromNode(method), elementTarget)
	}

	@Override
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz) {
		return new DeadCode(clazz.nameAsString, ClassHelper.createFullSignature(clazz),
				sourcePath, SourceRange.fromNode(clazz), elementTarget)
	}

	@Override
	FieldSpecific copy(FieldDeclaration field) {
		def variables = field.variables
		def newName = variables.size() <= 0 ? name : variables.size() == 1 ?
				variables[0].nameAsString : findSimilarVariable(variables).nameAsString

		return new DeadCode(newName, field.toString(Printer.NO_COMMENTS),
				sourcePath, SourceRange.fromNode(field), elementTarget)
	}

	private VariableDeclarator findSimilarVariable(NodeList<VariableDeclarator> variables) {
		variables[variables
				.collect { Strings.distance(name, it.nameAsString) }
				.indexed()
				.min { Map.Entry<Integer, Integer> a -> a.value }
				.key]
	}

	@Override
	LocalSpecific copy(Statement statement) {
		if (statement instanceof ExpressionStmt) {
			return copy(statement.expression)
		}
		return this
	}

	@Override
	LocalSpecific copy(Expression expression) {
		if (expression instanceof VariableDeclarationExpr) {
			def signature = (expression as VariableDeclarationExpr).toString(Printer.NO_COMMENTS)
			return new DeadCode(signature, signature, sourcePath, SourceRange.fromNode(expression), elementTarget)
		}
		return this
	}
}
