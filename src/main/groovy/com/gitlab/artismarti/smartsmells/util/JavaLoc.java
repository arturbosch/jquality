package com.gitlab.artismarti.smartsmells.util;

import java.util.List;

/**
 * Copied and refactored from my utils/loc project.
 *
 * @author artur
 */
public class JavaLoc {

	private String[] comments = new String[]{"//", "/*", "*/", "*"};
	private String[] escapes = new String[]{"import", "package"};

	public int analyze(List<String> lines, boolean isCommentMode, boolean isFullMode) {

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
				openedBrackets++;
			}

			if (trimmed.contains("}")) {
				closedBrackets++;
			}
		}

		int div = openedBrackets - closedBrackets;

		if (div == 0) {
			counter += openedBrackets;
		} else {
			counter = -1;
		}

		return counter;
	}

	private boolean isEscaped(String trimmed, String[] rules) {
		for (String rule : rules) {
			if (trimmed.startsWith(rule)) {
				return true;
			}
		}
		return false;
	}
}
