package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange

/**
 * @author Artur Bosch
 */
@CompileStatic
@Canonical
class MethodInfo implements HasMetrics {

	final String name
	final String declarationString
	final String signature

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	MethodInfo(String name,
			   String declarationString,
			   String signature,
			   Map<String, Metric> metrics,
			   SourcePath sourcePath,
			   SourceRange sourceRange) {
		this.name = name
		this.declarationString = declarationString
		this.signature = signature
		this.metrics = metrics
		this.sourcePath = sourcePath
		this.sourceRange = sourceRange
	}
}
