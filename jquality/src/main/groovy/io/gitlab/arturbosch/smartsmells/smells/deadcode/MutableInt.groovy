package io.gitlab.arturbosch.smartsmells.smells.deadcode

import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
class MutableInt {
	int value = 0

	void increment() { ++value }

	int get() { return value }
}
