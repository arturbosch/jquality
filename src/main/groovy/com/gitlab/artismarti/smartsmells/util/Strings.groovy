package com.gitlab.artismarti.smartsmells.util

/**
 * @author artur
 */
class Strings {

	private Strings() {
	}

	static String substringBefore(final String string, final String delimiter) {
		final int pos = string.indexOf(delimiter);
		return pos >= 0 ? string.substring(0, pos) : string;
	}

	static String substringAfter(final String string, final String delimiter) {
		final int pos = string.indexOf(delimiter);
		return pos >= 0 ? string.substring(pos + delimiter.length()) : "";
	}

}
