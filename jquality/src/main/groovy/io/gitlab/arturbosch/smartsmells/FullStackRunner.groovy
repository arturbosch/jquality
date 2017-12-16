package io.gitlab.arturbosch.smartsmells

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade
import io.gitlab.arturbosch.smartsmells.api.SmellResult

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
@CompileStatic
class FullStackRunner extends Runner {

	FullStackRunner(Path project, Optional<Path> outputPath, List<String> filters) {
		super(project, outputPath, filters)
	}

	@Override
	SmellResult run() {
		return run(DetectorFacade.builder()
				.withFilters(filters)
				.fullStackFacade())
	}

}
