package io.gitlab.arturbosch.jpal.ast

/**
 * Is thrown when the declaring compilation unit of a node cannot be found but is
 * needed for resolution.
 *
 * @author Artur Bosch
 */
class CompilationUnitNotFoundError extends RuntimeException {

	CompilationUnitNotFoundError(String message) {
		super(message)
	}
}