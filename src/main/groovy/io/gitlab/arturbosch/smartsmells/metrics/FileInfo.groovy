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
class FileInfo implements HasMetrics {

	final Set<ClassInfo> classes

	@Delegate
	final SourcePath sourcePath
	@Delegate
	final SourceRange sourceRange

	FileInfo(Set<ClassInfo> classes,
			 SourcePath sourcePath,
			 SourceRange sourceRange,
			 Map<String, Metric> metrics = Collections.emptyMap()) {
		this.sourcePath = sourcePath
		this.sourceRange = sourceRange
		this.classes = classes
		this.metrics = metrics
	}

	@Override
	String toString() {
		return "FileInfo{relativePath=$relativePath\n\t" +
				classes.collect { it.toString() }.join("\n\t") +
				'}'
	}
}
