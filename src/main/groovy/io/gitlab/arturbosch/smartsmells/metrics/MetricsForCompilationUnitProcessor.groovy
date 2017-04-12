package io.gitlab.arturbosch.smartsmells.metrics

import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationInfoProcessor
import io.gitlab.arturbosch.jpal.resolution.Resolver

/**
 * @author Artur Bosch
 */
class MetricsForCompilationUnitProcessor implements CompilationInfoProcessor<CompilationUnitMetrics> {

	private Resolver resolver

	MetricsForCompilationUnitProcessor(Resolver resolver) {
		this.resolver = resolver
	}

	@Override
	CompilationUnitMetrics process(CompilationInfo compilationInfo) {
		def visitor = new ClassInfoVisitor(false)
		visitor.initialize(compilationInfo)
		visitor.visit(compilationInfo.unit, resolver)
		return new CompilationUnitMetrics(visitor.getSmells())
	}

}
