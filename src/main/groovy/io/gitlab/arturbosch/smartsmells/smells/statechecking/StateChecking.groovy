package io.gitlab.arturbosch.smartsmells.smells.statechecking

import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.stmt.SwitchStmt
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
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
class StateChecking implements DetectionResult, LocalSpecific {

	static String INSTANCE_OF = "Replace instanceof conditions with polymorphism"
	static String SUBTYPING = "Replace state checking variables with subtyping"

	String inScope //ClassSignature#MethodSignature
	List<String> cases = new ArrayList<>()
	String type

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	ElementTarget elementTarget = ElementTarget.CLASS

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}

	@Override
	String name() {
		return type
	}

	String signature() {
		return "$inScope#${cases.join(", ")}"
	}

	@Override
	String asCompactString() {
		return "StateChecking \n\n$type"
	}

	@Override
	String asComparableString() {
		return "${javaClassName()}$type\$${signature()}"
	}

	@Override
	LocalSpecific copy(Statement statement) {
		if (statement instanceof IfStmt) return copyStatement(statement) {
			CasesCollector.ofIf(it as IfStmt)
		} else if (statement instanceof SwitchStmt) return copyStatement(statement) {
			CasesCollector.ofSwitch(it as SwitchStmt)
		}

		return this
	}

	private StateChecking copyStatement(Statement statement, Closure<List<String>> casesCreator) {
		def method = NodeHelper.findDeclaringMethod(statement).orElse(null)
		if (!method) return this
		def clazz = NodeHelper.findDeclaringClass(method).orElse(null)
		if (!clazz) return this
		def signature = ClassHelper.createFullSignature(clazz) + "#" + method.declarationAsString
		def cases = casesCreator(statement)
		return new StateChecking(signature, cases, type, sourcePath, SourceRange.fromNode(statement), elementTarget)
	}

	@Override
	LocalSpecific copy(Expression expression) {
		return this
	}

}
