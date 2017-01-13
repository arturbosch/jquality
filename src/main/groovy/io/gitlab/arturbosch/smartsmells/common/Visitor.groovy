package io.gitlab.arturbosch.smartsmells.common

import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.PackageScope
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.Resolver

import java.nio.file.Path

/**
 * @author artur
 */
abstract class Visitor<T extends DetectionResult> extends VoidVisitorAdapter<Resolver> {

	protected Path path
	protected CompilationInfo info

	private Set<T> smells = new HashSet<>()

	Set<T> getSmells() {
		return smells
	}

	void initialize(CompilationInfo info) {
		this.info = info
		path = info.path
	}

	protected void visit(CompilationInfo info, Resolver resolver) {
		visit(info.unit, resolver)
	}
}
