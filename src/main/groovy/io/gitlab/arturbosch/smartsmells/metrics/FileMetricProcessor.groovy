package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.CompileStatic
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
		detector.initialize(info)
		detector.visit(info, resolver)
		return new FileInfo(info.path, info.relativePath, detector.classes)
	}

}
