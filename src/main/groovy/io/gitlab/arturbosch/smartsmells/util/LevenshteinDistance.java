package io.gitlab.arturbosch.smartsmells.util;

/**
 * Copied from commons-lang.
 * https://github.com/apache/commons-lang/blob/1571050a196198f336ae487ee3b6df629d3ee9da/src/main/java/org/apache/commons/lang3/StringUtils.java
 *
 * @author Artur Bosch
 */
public class LevenshteinDistance {

	private LevenshteinDistance() {
	}

	static int compute(CharSequence s, CharSequence t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		int n = s.length();
		int m = t.length();

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		if (n > m) {
			// swap the input strings to consume less memory
			final CharSequence tmp = s;
			s = t;
			t = tmp;
			n = m;
			m = t.length();
		}

		final int p[] = new int[n + 1];
		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t
		int upper_left;
		int upper;

		char t_j; // jth character of t
		int cost;

		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			upper_left = p[0];
			t_j = t.charAt(j - 1);
			p[0] = j;

			for (i = 1; i <= n; i++) {
				upper = p[i];
				cost = s.charAt(i - 1) == t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
				p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upper_left + cost);
				upper_left = upper;
			}
		}

		return p[n];
	}

}
