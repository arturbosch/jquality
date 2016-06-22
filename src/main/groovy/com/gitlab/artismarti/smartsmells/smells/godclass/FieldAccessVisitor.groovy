package com.gitlab.artismarti.smartsmells.smells.godclass

import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
/**
 * @author artur
 */
class FieldAccessVisitor extends VoidVisitorAdapter<Set<String>> {

	private Set<String> fieldNames = new ArrayList<>()
	private List<String> classFieldNames

	FieldAccessVisitor(List<String> classFieldNames) {
		this.classFieldNames = classFieldNames
	}

	@Override
	void visit(NameExpr n, Set<String> arg) {
		def name = n.name
		if (classFieldNames.contains(name)) {
			fieldNames.add(name)
		}
		super.visit(n, arg)
	}

//	@Override
//	void visit(FieldAccessExpr n, Set<String> arg) {
//		def field = n.field
//		if (classFieldNames.contains(field)) {
//			fieldNames.add(field)
//		}
//	}
//
//	@Override
//	void visit(MethodCallExpr n, Set<String> arg) {
//		Optional.ofNullable(n.scope)
//				.filter { classFieldNames.contains(it.toStringWithoutComments()) }
//				.ifPresent { fieldNames.add(it.toStringWithoutComments()) }
//	}

	Set<String> getFieldNames() {
		return fieldNames
	}
}
