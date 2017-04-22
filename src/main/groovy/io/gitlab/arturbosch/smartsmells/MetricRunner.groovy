package io.gitlab.arturbosch.smartsmells

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.JarLoader
import io.gitlab.arturbosch.smartsmells.api.MetricFacade
import io.gitlab.arturbosch.smartsmells.api.MetricLoader
import io.gitlab.arturbosch.smartsmells.api.MetricResult
import io.gitlab.arturbosch.smartsmells.api.SmellResult
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.config.dsl.DetectorConfigDsl
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult

import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
@CompileStatic
class MetricRunner extends Runner {

	private MetricFacade facade

	MetricRunner(DetectorConfigDsl dsl) {
		super(dsl.input, dsl.output, dsl.filters)
		def jars = dsl.jars.collect { Paths.get(it) }
		facade = MetricFacade.builder().fromConfig(dsl)
				.withFilters(filters)
				.withLoader(new MetricLoader(new JarLoader(jars)))
				.fullStackFacade()
	}

	@Override
	SmellResult run() {
		def classInfos = facade.run(project)
		def metrics = [new MetricResult(MetricFacade.average(classInfos))] as List<DetectionResult>
		HashMap<Smell, List<DetectionResult>> map = new HashMap<>()
		map.put(Smell.UNKNOWN, metrics)
		return new SmellResult(map)
	}
}
