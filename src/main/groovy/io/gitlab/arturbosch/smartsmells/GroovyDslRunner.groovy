package io.gitlab.arturbosch.smartsmells

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade
import io.gitlab.arturbosch.smartsmells.api.SmellResult
import io.gitlab.arturbosch.smartsmells.config.DetectorConfig
import io.gitlab.arturbosch.smartsmells.config.dsl.DetectorConfigDsl

/**
 * @author Artur Bosch
 */
@CompileStatic
class GroovyDslRunner extends Runner {

	private DetectorConfig config

	GroovyDslRunner(DetectorConfigDsl dsl) {
		super(dsl.input, dsl.output, dsl.filters)
		config = dsl.build()
	}

	@Override
	SmellResult run() {
		return run(DetectorFacade.builder()
				.withFilters(filters)
				.fromConfig(config)
				.build())
	}

}
