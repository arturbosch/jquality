package io.gitlab.arturbosch.smartsmells.config.dsl

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * @author Artur Bosch
 */
@CompileStatic
@ToString
class FiltersDelegate {
	final List<String> filters = new ArrayList<>()

	void filter(String name) {
		filters.add(name)
	}
}
