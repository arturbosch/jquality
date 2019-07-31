package io.gitlab.arturbosch.smartsmells.smells.deadcode


import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
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
	void visit(MethodCallExpr n, Object arg) {
		methodsToReferenceCount.get(n.nameAsString)?.increment()
		super.visit(n, arg)
	}

	@Override
	void visit(NameExpr n, Object arg) {
		def name = n.name.identifier
		checkOccurrence(fieldsToReferenceCount, name)
		checkOccurrence(parameterToReferenceCount, name)
		checkOccurrence(localeVariableToReferenceCount, name)
		super.visit(n, arg)
	}

	private static void checkOccurrence(Map<String, MutableInt> map, String expr) {
		map.entrySet().stream()
				.filter { expr.contains(it.key) }
				.forEach { it.value.increment() }
	}
}
