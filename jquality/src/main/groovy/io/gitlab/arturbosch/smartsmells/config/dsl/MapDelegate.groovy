package io.gitlab.arturbosch.smartsmells.config.dsl

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * @author Artur Bosch
 */
@CompileStatic
@ToString
class MapDelegate implements PrintableDelegate {
	final Map<String, String> values

	MapDelegate(final Map<String, String> values = new HashMap<>()) {
		this.values = values
	}

	void let(String key, String value) {
		values.put(key, value)
	}

	@Override
	String print(int indent) {
		def tabs = tabsForIndent(indent)
		return values.collect { "${tabs}let('$it.key', '$it.value')" }
				.join("\n")
	}
}
