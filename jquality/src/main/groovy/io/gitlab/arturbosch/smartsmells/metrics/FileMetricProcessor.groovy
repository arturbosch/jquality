package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationInfoProcessor
import io.gitlab.arturbosch.jpal.resolution.Resolver

/**
 * @author Artur Bosch
 */
@CompileStatic
class FileMetricProcessor implements CompilationInfoProcessor {

	@Override
	void process(CompilationInfo info, Resolver resolver) {
		new MetricDetector().visit(info, resolver)
	}
}
