package io.gitlab.arturbosch.smartsmells.config.dsl

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * @author Artur Bosch
 */
@CompileStatic
@ToString
class DetectorsDelegate implements PrintableDelegate {

	final Map<String, Map<String, String>> values

	DetectorsDelegate(final Map<String, Map<String, String>> values = new HashMap<>()) {
		this.values = values
	}

	void detector(String name, Closure closure) {
		def mapDelegate = new MapDelegate()
		closure.delegate = mapDelegate
		closure.resolveStrategy = Closure.DELEGATE_ONLY
		closure()
		values.put(name, mapDelegate.values)
	}

	@Override
	String print(int indent) {
		def tabs = tabsForIndent(indent)
		return values.collect {
			"${tabs}detector('$it.key') {\n" +
					new MapDelegate(it.value).print(indent + 1) +
					"\n$tabs}"
		}.join("\n")
	}
}
