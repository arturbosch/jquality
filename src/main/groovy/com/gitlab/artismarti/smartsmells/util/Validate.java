package com.gitlab.artismarti.smartsmells.util;

/**
 * @author artur
 */
public final class Validate {

	private Validate() {
	}

	public static void isTrue(final boolean expression, final String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	public static <T> T notNull(T object) {
		if (object == null) {
			throw new IllegalArgumentException("Provided parameter is null!");
		}
		return object;
	}

	public static <T> T notNull(T object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
		return object;
	}
}
