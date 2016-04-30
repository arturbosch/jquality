package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ast.visitor.VoidVisitorAdapter

import java.nio.file.Path

/**
 * @author artur
 */
abstract class Visitor<T> extends VoidVisitorAdapter<Object> {

	protected Path startPath
	protected Path path

	private List<T> smells = new ArrayList<>()

	Visitor(Path path) {
		this.path = path
	}

	List<T> getSmells() {
		return smells
	}
}
