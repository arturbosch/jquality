package io.gitlab.arturbosch.smartsmells.common

import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.visitor.InternalVisitor
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult

/**
 * @author artur
 */
abstract class Visitor<T extends DetectionResult> extends InternalVisitor {

	private Set<T> smells = new HashSet<>()

	Set<T> getSmells() {
		return smells
	}

	protected void visit(CompilationInfo info, Resolver resolver) {
		visit(info.unit, resolver)
	}

}
