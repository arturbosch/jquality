package io.gitlab.arturbosch.smartsmells.config.dsl

import groovy.transform.CompileStatic
import groovy.transform.ToString
import io.gitlab.arturbosch.smartsmells.config.DetectorConfig

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
@CompileStatic
@ToString
class DetectorConfigDsl {

	Map<String, Map<String, String>> values
	Path input
	Optional<Path> output = Optional.empty()
	List<String> filters

	/**
	 * Should be called before using this object!
	 */
	void validate() {
		ValidateDsl.isTrue(Files.exists(input), "Input path $input does not exist!")
	}

	DetectorConfig build() {
		return new DetectorConfig(values)
	}

	void input(String project) {
		ValidateDsl.notNull(project)
		def path = Paths.get(project)
		input = path
	}

	void output(String xml) {
		ValidateDsl.notNull(xml)
		def path = Paths.get(xml)
		output = Optional.of(path)
	}

	void filters(Closure closure) {
		def filtersDelegate = new FiltersDelegate()
		closure.delegate = filtersDelegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure()
		filters = filtersDelegate.filters
	}

	void detectors(Closure closure) {
		def detectorsDelegate = new DetectorsDelegate()
		closure.delegate = detectorsDelegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure()
		values = detectorsDelegate.values
	}
}