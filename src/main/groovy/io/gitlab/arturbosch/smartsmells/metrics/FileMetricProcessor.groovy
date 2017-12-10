package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationInfoProcessor
import io.gitlab.arturbosch.jpal.resolution.Resolver

/**
 * @author Artur Bosch
 */
@CompileStatic
class FileMetricProcessor implements CompilationInfoProcessor<FileInfo> {

	private ClassInfoVisitor detector

	FileMetricProcessor(ClassInfoVisitor detector) {
		this.detector = detector
	}

	@Override
	FileInfo process(CompilationInfo info, Resolver resolver) {
		detector.visit(info, resolver)
		return new FileInfo(detector.classes, SourcePath.of(info), SourceRange.fromNode(info.unit))
	}

}
