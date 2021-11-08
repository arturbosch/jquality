package io.gitlab.arturbosch.jpal.internal

import groovy.transform.CompileStatic

/**
 * @author artur
 */
@CompileStatic
final class Validate {

	private Validate() {
	}

	static void isTrue(final boolean expression, final String message) {
		if (!expression) {
			throw new IllegalArgumentException(message)
		}
	}

	static notEmpty(String object) {
		if (object == null || object.isEmpty()) {
			throw new IllegalArgumentException("Provided argument is null!")
		}
		return object
	}

	static <T> T notNull(T object) {
		if (object == null) {
			throw new IllegalArgumentException("Provided argument is null!")
		}
		return object
	}

	static <T> T notNull(T object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message)
		}
		return object
	}
}
