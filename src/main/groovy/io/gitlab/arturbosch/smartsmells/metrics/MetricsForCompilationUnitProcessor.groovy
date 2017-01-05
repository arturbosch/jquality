package io.gitlab.arturbosch.smartsmells.metrics

import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationInfoProcessor

/**
 * @author Artur Bosch
 */
class MetricsForCompilationUnitProcessor implements CompilationInfoProcessor<CompilationUnitMetrics> {

	@Override
	CompilationUnitMetrics process(CompilationInfo compilationInfo) {
		def visitor = new ClassInfoVisitor(compilationInfo.path, false)
		visitor.visit(compilationInfo.unit, null)
		return new CompilationUnitMetrics(visitor.getSmells())
	}

}
