package io.gitlab.arturbosch.smartsmells.smells

import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
enum ElementTarget {
	FILE, TWO_CLASSES, CLASS, METHOD, FIELD, PARAMETER, LOCAL, ANY, NOT_SPECIFIED
	// not specified case should be ignored in analysis
}
