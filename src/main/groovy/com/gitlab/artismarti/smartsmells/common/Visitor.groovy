package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ast.visitor.VoidVisitorAdapter

import java.nio.file.Path

/**
 * @author artur
 */
abstract class Visitor<T> extends VoidVisitorAdapter<Object> {

	protected Path path

	Visitor(Path path) {
		this.path = path
	}

	List<T> smells = new ArrayList<>()
}
