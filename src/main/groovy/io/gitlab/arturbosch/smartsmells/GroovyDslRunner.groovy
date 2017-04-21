package io.gitlab.arturbosch.smartsmells

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade
import io.gitlab.arturbosch.smartsmells.api.DetectorLoader
import io.gitlab.arturbosch.smartsmells.api.JarLoader
import io.gitlab.arturbosch.smartsmells.api.SmellResult
import io.gitlab.arturbosch.smartsmells.config.DetectorConfig
import io.gitlab.arturbosch.smartsmells.config.dsl.DetectorConfigDsl

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
@CompileStatic
class GroovyDslRunner extends Runner {

	private DetectorConfig config
	private List<Path> jars

	GroovyDslRunner(DetectorConfigDsl dsl) {
		super(dsl.input, dsl.output, dsl.filters)
		config = dsl.build()
		jars = dsl.jars.collect { Paths.get(it) }
	}

	@Override
	SmellResult run() {
		return run(DetectorFacade.builder()
				.withFilters(filters)
				.fromConfig(config)
				.withLoader(new DetectorLoader(new JarLoader(jars)))
				.build())
	}

}
