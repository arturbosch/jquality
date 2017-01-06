package io.gitlab.arturbosch.smartsmells.util

/**
 * @author artur
 */
class Strings {

	private Strings() {
	}

	static String substringBefore(final String string, final String delimiter) {
		final int pos = string.indexOf(delimiter)
		return pos >= 0 ? string.substring(0, pos) : string
	}

	static String substringAfter(final String string, final String delimiter) {
		final int pos = string.indexOf(delimiter)
		return pos >= 0 ? string.substring(pos + delimiter.length()) : ""
	}

	static boolean isTrue(String value) {
		return value != null && !value.isEmpty() && "true".equalsIgnoreCase(value)
	}

	static boolean isEmpty(final String str) {
		return str == null || str.length() == 0
	}

	static int amountOf(String text, String part) {
		if (isEmpty(text) || isEmpty(part)) {
			return 0
		}

		int count = 0
		for (int pos = 0; (pos = text.indexOf(part, pos)) != -1; count++) {
			pos += part.length()
		}

		return count
	}
}
