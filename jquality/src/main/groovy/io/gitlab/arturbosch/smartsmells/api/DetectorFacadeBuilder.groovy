package io.gitlab.arturbosch.smartsmells.api

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.config.DetectorConfig
import io.gitlab.arturbosch.smartsmells.config.DetectorInitializer
import io.gitlab.arturbosch.smartsmells.config.Dsl
import io.gitlab.arturbosch.smartsmells.util.Validate

import java.util.concurrent.ExecutorService

/**
 * @author Artur Bosch
 */
@CompileStatic
class DetectorFacadeBuilder {

	private List<Detector> detectors = new LinkedList<>()
	private List<String> filters = new ArrayList<String>()
	private DetectorConfig config = null
	private ExecutorService executorService = null

	DetectorFacadeBuilder with(Detector detector) {
		Validate.notNull(detector)
		detectors.add(detector)
		return this
	}

	DetectorFacadeBuilder withLoader(DetectorLoader loader) {
		Validate.notNull(loader)
		detectors.addAll(loader.load())
		return this
	}

	DetectorFacade fullStackFacade() {
		def detectorConfig = Dsl.INSTANCE().build()
		return fromConfig(detectorConfig).build()
	}

	DetectorFacadeBuilder withFilters(List<String> filters) {
		this.filters = Validate.notNull(filters)
		return this
	}

	DetectorFacadeBuilder fromConfig(final DetectorConfig config) {
		Validate.notNull(config, "Configuration must not be null!")
		detectors = DetectorInitializer.init(config)
		this.config = config
		return this
	}

	DetectorFacadeBuilder withExecutor(final ExecutorService executorService) {
		Validate.notNull(executorService, "Executor must not be null.")
		this.executorService = executorService
		return this
	}

	DetectorFacade build() {
		return new DetectorFacade(detectors, config, filters, executorService)
	}
}
