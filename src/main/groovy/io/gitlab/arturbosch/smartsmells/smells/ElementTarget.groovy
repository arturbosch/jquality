package io.gitlab.arturbosch.smartsmells.smells

/**
 * @author Artur Bosch
 */
enum ElementTarget {
	FILE, TWO_CLASSES, CLASS, METHOD, FIELD, PARAMETER, LOCAL, ANY, NOT_SPECIFIED // not specified case should be ignored in analysis
}