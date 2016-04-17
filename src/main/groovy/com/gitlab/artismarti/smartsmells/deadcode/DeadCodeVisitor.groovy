package com.gitlab.artismarti.smartsmells.deadcode

import com.github.javaparser.ast.CompilationUnit
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

		super.visit(n, arg)
	}
}
