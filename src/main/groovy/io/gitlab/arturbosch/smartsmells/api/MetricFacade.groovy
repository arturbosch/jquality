package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.MetricsForCompilationUnitProcessor

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class MetricFacade {

	private List<String> filters

	MetricFacade(List<String> filters = null) {
		this.filters = filters
	}

	List<ClassInfo> run(Path root) {
		def builder = DetectorFacade.builder()
		if (filters) builder.withFilters(filters)
		def facade = builder.build()
		def storage = root ? JPAL.initializedUpdatable(root, null, facade.filters) : JPAL.updatable(null, facade.filters)
		def resolver = new Resolver(storage)
		def processor = new MetricsForCompilationUnitProcessor(resolver)
		def classInfos = storage.allCompilationInfo
				.collect { processor.process(it) }
				.collect { it.infos }
				.flatten()
		return classInfos as List<ClassInfo>
	}
}
