package io.gitlab.arturbosch.smartsmells.smells.complexcondition

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.EnumDeclaration
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.DoStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.WhileStmt
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.EnumHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.smells.statechecking.StateCheckingVisitor

/**
 * @author Artur Bosch
 */
@CompileStatic
class ComplexConditionVisitor extends Visitor<ComplexCondition> {

	private String currentClassName = ""
	private int threshold

	ComplexConditionVisitor(int threshold) {
		this.threshold = threshold
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver arg) {
		currentClassName = ClassHelper.createFullSignature(n)
		super.visit(n, arg)
	}

	@Override
	void visit(EnumDeclaration n, Resolver arg) {
		currentClassName = EnumHelper.createFullSignature(n)
		super.visit(n, arg)
	}

	@Override
	void visit(IfStmt n, Resolver arg) {
		checkCondition(n.condition)
		super.visit(n, arg)
	}

	private void checkCondition(Expression expression) {
		String condition = expression.toString(Printer.NO_COMMENTS)
		def cases = new HashSet<String>()
		Conditions.whileBinaryExpression(cases, expression)
		if (cases.size() > threshold) {
			def methodName = NodeHelper.findDeclaringMethod(expression)
					.map { it.declarationAsString }
					.orElse(StateCheckingVisitor.UNKNOWN_METHOD)
			def scope = currentClassName + "#" + methodName
			smells.add(new ComplexCondition(scope, condition, cases, threshold,
					SourcePath.of(info), SourceRange.fromNode(expression), ElementTarget.LOCAL))
		}
	}

	@Override
	void visit(DoStmt n, Resolver arg) {
		checkCondition(n.condition)
		super.visit(n, arg)
	}

	@Override
	void visit(WhileStmt n, Resolver arg) {
		checkCondition(n.condition)
		super.visit(n, arg)
	}
}
