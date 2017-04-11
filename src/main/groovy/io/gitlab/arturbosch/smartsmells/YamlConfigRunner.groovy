package io.gitlab.arturbosch.smartsmells

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade
import io.gitlab.arturbosch.smartsmells.api.SmellResult
import io.gitlab.arturbosch.smartsmells.config.DetectorConfig

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@CompileStatic
class YamlConfigRunner extends Runner {

	private DetectorConfig config

	YamlConfigRunner(Path configPath,
					 Path project,
					 Optional<Path> outputPath,
					 List<String> filters) {
		super(project, outputPath, filters)
		config = DetectorConfig.load(configPath)
	}

	@Override
	SmellResult run() {
		return run(DetectorFacade.builder()
				.withFilters(filters)
				.fromConfig(config)
				.build())
	}
}
