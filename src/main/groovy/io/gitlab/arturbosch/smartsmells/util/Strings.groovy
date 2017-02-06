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

	static int distance(String str1, String str2) {
		def str1_len = str1.length()
		def str2_len = str2.length()
		int[][] distance = new int[str1_len + 1][str2_len + 1]
		(str1_len + 1).times { distance[it][0] = it }
		(str2_len + 1).times { distance[0][it] = it }
		(1..str1_len).each { i ->
			(1..str2_len).each { j ->
				distance[i][j] = [distance[i-1][j]+1, distance[i][j-1]+1, str1[i-1]==str2[j-1]?distance[i-1][j-1]:distance[i-1][j-1]+1].min()
			}
		}
		distance[str1_len][str2_len]
	}
}
