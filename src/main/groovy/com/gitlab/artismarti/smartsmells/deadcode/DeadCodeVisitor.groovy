package com.gitlab.artismarti.smartsmells.deadcode

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.MethodReferenceExpr
import com.gitlab.artismarti.smartsmells.common.NodeHelper
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path
import java.util.function.Function
import java.util.stream.Collectors

/**
 * @author artur
 */
class DeadCodeVisitor extends Visitor<DeadCode> {

	private boolean onlyPrivate

	private Map<String, Integer> methods
	private Map<String, Integer> fields

	DeadCodeVisitor(Path path, boolean onlyPrivate) {
		super(path)
		this.onlyPrivate = onlyPrivate
	}

	@Override
	void visit(CompilationUnit n, Object arg) {
		methods = NodeHelper.findPrivateMethods(n)
				.stream()
				.collect(Collectors.toMap(Function.identity(), { method -> 0 }))
		fields = NodeHelper.findPrivateFields(n)
				.stream()
				.collect(Collectors.toMap(Function.identity(), { method -> 0 }))

		println methods
		println fields
		super.visit(n, arg)

		println methods
		println fields
	}

	@Override
	void visit(MethodReferenceExpr n, Object arg) {
		println n.identifier
		methods.computeIfPresent(n.identifier, { key, value -> value + 1 })
		super.visit(n, arg)
	}

	@Override
	void visit(MethodCallExpr n, Object arg) {
		methods.computeIfPresent(n.name, { key, value -> value + 1 })
		fields.computeIfPresent(n.scope, { key, value -> value + 1 })
		n.args.each { fields.computeIfPresent(it.toStringWithoutComments(), { key, value -> value + 1 }) }
		super.visit(n, arg)
	}

	@Override
	void visit(FieldAccessExpr n, Object arg) {
		println n.field
		fields.computeIfPresent(n.field, { key, value -> value + 1 })
		super.visit(n, arg)
	}
}
