package io.gitlab.arturbosch.smartsmells.config.dsl

import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
class DetectorConfigDslError extends RuntimeException {
	DetectorConfigDslError(String message) {
		super(message)
	}
}
