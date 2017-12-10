package io.gitlab.arturbosch.smartsmells

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.MetricFacade
import io.gitlab.arturbosch.smartsmells.api.MetricResult
import io.gitlab.arturbosch.smartsmells.api.SmellResult
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.config.dsl.DetectorConfigDsl
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult

/**
 * @author Artur Bosch
 */
@CompileStatic
class MetricRunner extends Runner {

	private MetricFacade facade

	MetricRunner(DetectorConfigDsl dsl) {
		super(dsl.input, dsl.output, dsl.filters)
		facade = new MetricFacade()
	}

	@Override
	SmellResult run() {
		def classInfos = facade.run(project, filters)
		def metrics = [new MetricResult(MetricFacade.averageAndDeviation(classInfos))] as List<DetectionResult>
		HashMap<Smell, List<DetectionResult>> map = new HashMap<>()
		map.put(Smell.UNKNOWN, metrics)
		return new SmellResult(map)
	}
}
