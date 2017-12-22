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
class FileMetricProcessor implements CompilationInfoProcessor {

	@Override
	void setup(CompilationInfo info, Resolver resolver) {
		FileInfo fileInfo = new FileInfo(SourcePath.of(info), SourceRange.fromNode(info.unit))
		info.setData(FileInfo.KEY, fileInfo)
	}

	@Override
	void process(CompilationInfo info, Resolver resolver) {
		new MetricDetector().visit(info, resolver)
	}

	@Override
	void cleanup(CompilationInfo info, Resolver resolver) {
		new PostMetricDetector().visit(info, resolver)
	}
}
