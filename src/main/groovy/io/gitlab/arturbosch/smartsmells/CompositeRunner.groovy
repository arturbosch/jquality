package io.gitlab.arturbosch.smartsmells

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.SmellResult
import io.gitlab.arturbosch.smartsmells.config.dsl.DetectorConfigDsl

/**
 * @author Artur Bosch
 */
@CompileStatic
class CompositeRunner extends Runner {

	private List<Runner> runners

	CompositeRunner(DetectorConfigDsl dsl, List<Runner> runners) {
		super(dsl.input, dsl.output, dsl.filters)
		this.runners = runners
	}

	@Override
	SmellResult run() {
		return runners.collect { it.run() }
				.inject(new SmellResult()) { result, current -> result.merge(current) }
	}
}
