package io.gitlab.arturbosch.smartsmells.metrics.internal

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.util.Strings

/**
 * @author Artur Bosch
 */
@CompileStatic
class LinesOfCode {

	private static final String[] comments = ["//", "/*", "*/", "*"].toArray() as String[]
	private static final String[] escapes = ["import", "package"].toArray() as String[]

	int source
	int blank
	int comment
	int logical

	private boolean isComment

	int analyze(String[] lines) {
		int openedBrackets = 0, closedBrackets = 0

		for (String line : lines) {

			String trimmed = line.trim()

			if (trimmed.isEmpty()) {
				blank++
				continue
			}

			def commentEnd = trimmed.contains("*/")
			if (commentEnd) {
				isComment = false
			} else if (trimmed.startsWith("/*") && !commentEnd) {
				isComment = true
			}

			if (isEscaped(trimmed, comments)) {
				comment++
				continue
			}

			if (isEscaped(trimmed, escapes)) {
				source++
				continue
			}

			logical += Strings.amountOf(trimmed, ";")


			if (isComment) {
				comment++
			} else {
				source++
			}

			if (trimmed.startsWith("@")) {
				logical++
			}

			if (trimmed.contains("{")) {
				openedBrackets += Strings.amountOf(trimmed, "{")
			}

			if (trimmed.contains("}")) {
				closedBrackets += Strings.amountOf(trimmed, "}")
			}
		}

		if (openedBrackets == closedBrackets) {
			logical += openedBrackets
		} else {
			logical += Math.min(openedBrackets, closedBrackets)
		}
	}

	private static boolean isEscaped(String trimmed, String[] rules) {
		return rules.any { String it -> trimmed.startsWith(it) }
	}

}

