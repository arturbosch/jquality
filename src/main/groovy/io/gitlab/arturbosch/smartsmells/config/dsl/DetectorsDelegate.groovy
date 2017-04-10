package io.gitlab.arturbosch.smartsmells.config.dsl

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * @author Artur Bosch
 */
@CompileStatic
@ToString
class DetectorsDelegate {

	final Map<String, Map<String, String>> values = new HashMap<>()

	void detector(String name, Closure closure) {
		def mapDelegate = new MapDelegate()
		closure.delegate = mapDelegate
		closure.resolveStrategy = Closure.DELEGATE_ONLY
		closure()
		values.put(name, mapDelegate.values)
	}
}
