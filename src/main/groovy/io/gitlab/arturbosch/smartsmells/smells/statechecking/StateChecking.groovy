package io.gitlab.arturbosch.smartsmells.smells.statechecking

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
class StateChecking implements DetectionResult {

	String inMethod
	List<String> cases = new HashSet<>()

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	@Override
	String asCompactString() {
		return null
	}
}
