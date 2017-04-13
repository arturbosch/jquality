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

	FileMetricProcessor(Resolver resolver) {
		this.resolver = resolver
	}

	@Override
	FileInfo process(CompilationInfo info) {
		def visitor = new ClassInfoVisitor(false)
		visitor.initialize(info)
		visitor.visit(info.unit, resolver)
		return new FileInfo(info.path, info.relativePath, visitor.getSmells())
	}

}
