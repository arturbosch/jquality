package io.gitlab.arturbosch.smartsmells.smells.nestedblockdepth

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.DetectionResult

/**
 * @author Artur Bosch
 */
@Immutable
@ToString(includePackage = false)
class NestedBlockDepth implements DetectionResult {

	String methodName
	String methodSignature

	int depth
	int depthThreshold

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	@Override
	String asCompactString() {
		"NestedBlockDepth \n\ndepth: $depth with threshold: $depthThreshold"
	}
}
