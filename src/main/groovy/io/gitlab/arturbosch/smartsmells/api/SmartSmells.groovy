package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.jpal.core.UpdatableCompilationStorage
import io.gitlab.arturbosch.jpal.resolution.Resolver

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class SmartSmells {

	SmartSmells(UpdatableCompilationStorage storage,
				Resolver resolver,
				DetectorFacade detectorFacade,
				MetricFacade metricFacade) {

	}

	SmellResult executeForRoot(Path root) {

		return null
	}
}
