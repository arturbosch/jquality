package io.gitlab.arturbosch.smartsmells.smells.middleman

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.DetectionResult

/**
 * @author artur
 */
@Immutable
@ToString(includeNames = false, includePackage = false)
class MiddleMan implements DetectionResult {

	String name
	String signature

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	@Override
	String asCompactString() {
		"MiddleMan \n\nMethods only delegate to others. No real logic."
	}

}
