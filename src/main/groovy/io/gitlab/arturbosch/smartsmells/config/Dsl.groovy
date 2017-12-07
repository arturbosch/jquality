package io.gitlab.arturbosch.smartsmells.config

import io.gitlab.arturbosch.smartsmells.config.dsl.DetectorConfigDsl
import io.gitlab.arturbosch.smartsmells.config.dsl.DetectorConfigDslRunner
import io.gitlab.arturbosch.smartsmells.util.Validate

/**
 * @author Artur Bosch
 */
final class Dsl {

	static DetectorConfigDsl INSTANCE() {
		return Loader.INSTANCE
	}

	private Dsl() {}

	static class Loader {

		private static DetectorConfigDsl INSTANCE = loadDefault()

		static DetectorConfigDsl loadDefault() {
			def resource = getClass().getResource("/default-config.groovy")
			Validate.notNull(resource, "Loaded default config was unexpectedly null.")
			def configFile = new File(resource.toURI())
			return DetectorConfigDslRunner.execute(configFile)
		}
	}

}
