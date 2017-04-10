package io.gitlab.arturbosch.smartsmells.config.dsl

import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
class ValidateDsl {

	private ValidateDsl() {
	}

	static void isTrue(final boolean expression, final String message) {
		if (!expression) {
			throw new DetectorConfigDslError(message)
		}
	}

	static <T> T notNull(T object) {
		if (object == null) {
			throw new DetectorConfigDslError("Required parameter is null!")
		}
		return object
	}

}
