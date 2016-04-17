package com.gitlab.artismarti.smartsmells.deadcode

import com.github.javaparser.ast.CompilationUnit
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class DeadCodeVisitor extends Visitor<DeadCode> {

	private boolean onlyPrivate

	DeadCodeVisitor(Path path, boolean onlyPrivate) {
		super(path)
		this.onlyPrivate = onlyPrivate
	}

	@Override
	void visit(CompilationUnit n, Object arg) {
		super.visit(n, arg)
	}
}
