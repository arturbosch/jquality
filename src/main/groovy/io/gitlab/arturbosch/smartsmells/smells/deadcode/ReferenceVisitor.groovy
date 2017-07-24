package io.gitlab.arturbosch.smartsmells.smells.deadcode

import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.CastExpr
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.MethodReferenceExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.stmt.ForStmt
import com.github.javaparser.ast.stmt.ForeachStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.ReturnStmt
import com.github.javaparser.ast.stmt.SwitchStmt
import com.github.javaparser.ast.stmt.WhileStmt
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

/**
 * @author Artur Bosch
 */
class ReferenceVisitor extends VoidVisitorAdapter {

	private Map<String, MutableInt> methodsToReferenceCount
	private Map<String, MutableInt> fieldsToReferenceCount
	private Map<String, MutableInt> parameterToReferenceCount
	private Map<String, MutableInt> localeVariableToReferenceCount

	ReferenceVisitor(Map<String, MutableInt> methodsToReferenceCount,
					 Map<String, MutableInt> fieldsToReferenceCount,
					 Map<String, MutableInt> localeVariableToReferenceCount,
					 Map<String, MutableInt> parameterToReferenceCount) {
		this.localeVariableToReferenceCount = localeVariableToReferenceCount
		this.parameterToReferenceCount = parameterToReferenceCount
		this.fieldsToReferenceCount = fieldsToReferenceCount
		this.methodsToReferenceCount = methodsToReferenceCount
	}

	@Override
	void visit(MethodReferenceExpr n, Object arg) {
		methodsToReferenceCount.get(n.identifier)?.increment()
		super.visit(n, arg)
	}

	@Override
	void visit(AssignExpr n, Object arg) {
		checkArguments(n.value)
		super.visit(n, arg)
	}

	@Override
	void visit(CastExpr n, Object arg) {
		checkArguments(n.expression)
		super.visit(n, arg)
	}

	@Override
	void visit(BinaryExpr n, Object arg) {
		checkArguments(n.left)
		checkArguments(n.right)
		super.visit(n, arg)
	}

	@Override
	void visit(ReturnStmt n, Object arg) {
		n.expression.ifPresent { checkArguments(it) }
		super.visit(n, arg)
	}

	@Override
	void visit(MethodCallExpr n, Object arg) {
		methodsToReferenceCount.get(n.nameAsString)?.increment()

		n.scope.ifPresent {
			checkArguments(it)
		}

		n.arguments.each {
			checkArguments(it)
		}

		super.visit(n, arg)
	}

	@Override
	void visit(FieldAccessExpr n, Object arg) {
		fieldsToReferenceCount.get(n.nameAsString)?.increment()
		super.visit(n, arg)
	}

	@Override
	void visit(ObjectCreationExpr n, Object arg) {
		n.arguments.each {
			checkArguments(it)
		}
		super.visit(n, arg)
	}

	@Override
	void visit(ForeachStmt n, Object arg) {
		checkArguments(n.iterable)
		super.visit(n, arg)
	}

	@Override
	void visit(ForStmt n, Object arg) {
		n.compare.ifPresent { checkArguments(it) }
		super.visit(n, arg)
	}

	@Override
	void visit(IfStmt n, Object arg) {
		checkArguments(n.condition)
		super.visit(n, arg)
	}

	@Override
	void visit(WhileStmt n, Object arg) {
		checkArguments(n.condition)
		super.visit(n, arg)
	}

	@Override
	void visit(SwitchStmt n, Object arg) {
		checkArguments(n.selector)
		super.visit(n, arg)
	}

	private void checkArguments(Expression it) {
		Optional.ofNullable(it)
				.filter { it instanceof NameExpr }
				.map { it as NameExpr }
				.map { it.nameAsString }
				.ifPresent {
			checkOccurrence(fieldsToReferenceCount, it)
			checkOccurrence(parameterToReferenceCount, it)
			checkOccurrence(localeVariableToReferenceCount, it)
		}
	}

	private static void checkOccurrence(Map<String, MutableInt> map, String expr) {
		map.entrySet().stream()
				.filter { expr.contains(it.key) }
				.forEach { it.value.increment() }
	}

}