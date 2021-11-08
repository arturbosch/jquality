package io.gitlab.arturbosch.jpal.resolution.nested

import groovy.transform.CompileStatic

/**
 * Thrown by inner classes handler when the given compilation unit is invalid -
 * contains no classes.
 *
 * @author artur
 */
@CompileStatic
class NoClassesException extends RuntimeException {

	NoClassesException(String message) {
		super(message)
	}
}
