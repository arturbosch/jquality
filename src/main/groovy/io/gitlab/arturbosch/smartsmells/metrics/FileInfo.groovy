package io.gitlab.arturbosch.smartsmells.metrics

import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
class FileInfo implements DetectionResult {

	final String absolutePath
	final String relativePath
	final Set<ClassInfo> infos

	final ElementTarget elementTarget = ElementTarget.CLASS

	FileInfo(Set<ClassInfo> infos) {
		this.infos = infos
	}

	@Override
	String toString() {
		return "CompilationUnitMetrics{" +
				"infos=" + infos +
				'}'
	}

	@Override
	String asCompactString() {
		return null
	}

	@Override
	String asComparableString() {
		return null
	}

	@Override
	ElementTarget elementTarget() {
		return elementTarget
	}
}
