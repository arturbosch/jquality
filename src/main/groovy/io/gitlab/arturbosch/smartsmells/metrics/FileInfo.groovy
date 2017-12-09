package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@CompileStatic
class FileInfo implements DetectionResult {

	final Path absolutePath
	final Path relativePath
	final Set<ClassInfo> classes

	final ElementTarget elementTarget = ElementTarget.CLASS

	FileInfo(Path absolutePath, Path relativePath, Set<ClassInfo> classes) {
		this.absolutePath = absolutePath
		this.relativePath = relativePath
		this.classes = classes
	}

	@Override
	String toString() {
		return "FileInfo{relativePath=$relativePath\n\t" +
				classes.collect { it.toString() }.join("\n\t") +
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
