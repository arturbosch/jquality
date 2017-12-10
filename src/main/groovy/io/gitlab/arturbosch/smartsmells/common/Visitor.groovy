package io.gitlab.arturbosch.smartsmells.common

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.common.visitor.InternalVisitor
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult

/**
 * @author Artur Bosch
 */
@CompileStatic
abstract class Visitor<T extends DetectionResult> extends InternalVisitor {

	private Set<T> smells = new HashSet<>()

	Set<T> getSmells() {
		return smells
	}

	protected void report(T smell) {
		smells.add(smell)
	}
}
