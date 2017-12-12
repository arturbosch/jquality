package io.gitlab.arturbosch.smartsmells.smells

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.api.SmellExchange
import io.gitlab.arturbosch.smartsmells.smells.cycle.Cycle

/**
 * @author artur
 */
@CompileStatic
trait DetectionResult {

	abstract String asCompactString()

	abstract String asComparableString()

	abstract ElementTarget elementTarget()

	String asCliRdyString() {
		def positions = getPositions()
		def line = positions.startLine
		def column = positions.startColumn
		return getClass().simpleName + " - [${SmellExchange.extractIdentifier(this)}] -" +
				" at " + getRelativePathAsString() + ":$line:$column"
	}

	SourceRange getPositions() {
		return getAttribute(this, "sourceRange") as SourceRange
	}

	String getPathAsString() {
		return getAttribute(this, "sourcePath").toString()
	}


	String getRelativePathAsString() {
		return (getAttribute(this, "sourcePath") as SourcePath).relativePath.toString()
	}

	private static Object getAttribute(DetectionResult detectionResult, String name) {
		switch (detectionResult) {
			case Cycle: return SmellExchange.getAttribute(
					SmellExchange.getAttribute(detectionResult, "source") as DetectionResult, name)
			default: return SmellExchange.getAttribute(detectionResult, name)
		}
	}

	String javaClassName() {
		return getClass().simpleName + "\$${getRelativePathAsString()}\$"
	}
}
