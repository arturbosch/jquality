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

	private ClassInfoDetector detector

	FileMetricProcessor(ClassInfoDetector detector) {
		this.detector = detector
	}

	@Override
	FileInfo process(CompilationInfo info, Resolver resolver) {
		def classInfos = detector.execute(info, resolver)
		return new FileInfo(info.path, info.relativePath, classInfos)
	}

}
