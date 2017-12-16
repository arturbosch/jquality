package io.gitlab.arturbosch.smartsmells.smells.godclass

import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

/**
 * @author artur
 */
class FieldAccessVisitor extends VoidVisitorAdapter<Set<String>> {

	private Set<String> fieldNames = new HashSet<>()
	private Set<String> classFieldNames

	FieldAccessVisitor(Set<String> classFieldNames) {
		this.classFieldNames = classFieldNames
	}

	@Override
	void visit(NameExpr n, Set<String> arg) {
		def name = n.nameAsString
		if (classFieldNames.contains(name)) {
			fieldNames.add(name)
		}
		super.visit(n, arg)
	}

	Set<String> getFieldNames() {
		return fieldNames
	}
}
