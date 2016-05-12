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
}
