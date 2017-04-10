package io.gitlab.arturbosch.smartsmells.config.dsl

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * @author Artur Bosch
 */
@CompileStatic
@ToString
class MapDelegate {
	final Map<String, String> values = new HashMap<>()

	void let(String key, String value) {
		values.put(key, value)
	}
}
