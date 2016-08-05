package io.gitlab.arturbosch.smartsmells.smells.cycle

/**
 * @author artur
 */
class NoClassesException extends RuntimeException {

	NoClassesException() {
		super("Compilation unit is empty!")
	}
}
