package io.gitlab.arturbosch.smartsmells.common

import com.github.javaparser.ast.visitor.VoidVisitorAdapter

import java.nio.file.Path

/**
 * @author artur
 */
abstract class Visitor<T extends Smelly> extends VoidVisitorAdapter<Object> {

	protected Path path

	private Set<T> smells = new HashSet<>()

	Visitor(Path path) {
		this.path = path
	}

	Set<T> getSmells() {
		return smells
	}
}
