package io.gitlab.arturbosch.smartsmells.util;

import java.util.List;

/**
 * Copied and refactored from my utils/loc project.
 *
 * @author artur
 */
public class JavaLoc {

	private static final String[] comments = new String[]{"//", "/*", "*/", "*"};
	private static final String[] escapes = new String[]{"import", "package"};

	public static int analyze(List<String> lines, boolean isCommentMode, boolean isFullMode) {

		int counter = 0, openedBrackets = 0, closedBrackets = 0;
		boolean escape;

		for (String line : lines) {

			String trimmed = line.trim();

			if (trimmed.isEmpty()) {
				continue;
			}

			escape = isEscaped(trimmed, comments);
			if (isCommentMode && escape) {
				counter++;
			}

			if (escape) {
				continue;
			}

			escape = isEscaped(trimmed, escapes);
			if (escape && isFullMode) {
				counter++;
			}

			if (escape) {
				continue;
			}

			if (trimmed.contains(";")) {
				counter++;
			}

			if (trimmed.contains("{")) {
				openedBrackets += Strings.amountOf(trimmed, "{");
			}

			if (trimmed.contains("}")) {
				closedBrackets += Strings.amountOf(trimmed, "}");
			}
		}

		return counter + (openedBrackets - closedBrackets == 0 ? openedBrackets : -1);
	}

	private static boolean isEscaped(String trimmed, String[] rules) {
		for (String rule : rules) {
			if (trimmed.startsWith(rule)) {
				return true;
			}
		}
		return false;
	}
}
