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

	private Resolver resolver
	private ClassInfoDetector detector

	FileMetricProcessor(ClassInfoDetector detector, Resolver resolver) {
		this.detector = detector
		this.resolver = resolver
	}

	@Override
	FileInfo process(CompilationInfo info) {
		def classInfos = detector.execute(info, resolver)
		return new FileInfo(info.path, info.relativePath, classInfos)
	}

}
