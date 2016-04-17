package com.gitlab.artismarti.smartsmells.deadcode

import com.github.javaparser.ast.CompilationUnit
import com.gitlab.artismarti.smartsmells.common.NodeHelper
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class DeadCodeVisitor extends Visitor<DeadCode> {

	private boolean onlyPrivate

	List<String> methods
	List<String> fields

	DeadCodeVisitor(Path path, boolean onlyPrivate) {
		super(path)
		this.onlyPrivate = onlyPrivate
	}

	@Override
	void visit(CompilationUnit n, Object arg) {
		methods = NodeHelper.findPrivateMethods(n)
		fields = NodeHelper.findPrivateFields(n)
		super.visit(n, arg)
	}
}
