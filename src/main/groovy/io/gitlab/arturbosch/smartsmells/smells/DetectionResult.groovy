package io.gitlab.arturbosch.smartsmells.smells

import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.api.SmellExchange
import io.gitlab.arturbosch.smartsmells.smells.cycle.Cycle

/**
 * @author artur
 */
trait DetectionResult {

	abstract String asCompactString()

	SourceRange getPositions() {
		return getAttribute(this, "sourceRange") as SourceRange
	}

	String getPathAsString() {
		return getAttribute(this, "sourcePath").toString()
	}

	private static Object getAttribute(DetectionResult detectionResult, String name) {
		switch (detectionResult) {
			case Cycle: return SmellExchange.getAttribute(
					SmellExchange.getAttribute(detectionResult, "source") as DetectionResult, name)
			default: return SmellExchange.getAttribute(detectionResult, name)
		}
	}

}
