package io.gitlab.arturbosch.smartsmells.config.dsl

/**
 * @author Artur Bosch
 */
trait PrintableDelegate {
	abstract String print(int indent)

	String tabsForIndent(int number) {
		if (number < 1) return ""
		(1..number).collect { '\t' }.join("")
	}
}