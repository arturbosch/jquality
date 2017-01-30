package io.gitlab.arturbosch.smartsmells.smells.complexmethod

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethod

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class ComplexMethod implements DetectionResult {

	String name
	String signature
	int size
	int threshold

	@Delegate
	SourceRange sourceRange
	@Delegate
	SourcePath sourcePath

	@Override
	String asCompactString() {
		"ComplexMethod \n\nCyclomaticComplexity: $size"
	}

	static of(LongMethod lm) {
		new ComplexMethod(lm.name, lm.signature, lm.size, lm.threshold, lm.sourceRange, lm.sourcePath)
	}
}
