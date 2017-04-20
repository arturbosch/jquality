package io.gitlab.arturbosch.smartsmells.config.dsl

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * @author Artur Bosch
 */
@CompileStatic
@ToString
class FiltersDelegate implements PrintableDelegate {
	final List<String> filters

	FiltersDelegate(final List<String> filters = new ArrayList<>()) {
		this.filters = filters
	}

	void filter(String name) {
		filters.add(name)
	}

	@Override
	String print(int indent) {
		def tabs = tabsForIndent(indent)
		return "${tabs}filters {\n" +
				filters.collect { "${tabs}\tfilter '$it'" }.join("\n") +
				"\n${tabs}}"
	}
}
