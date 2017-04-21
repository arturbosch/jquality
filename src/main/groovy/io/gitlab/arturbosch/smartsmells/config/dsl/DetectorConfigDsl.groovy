package io.gitlab.arturbosch.smartsmells.config.dsl

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
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
@EqualsAndHashCode
class DetectorConfigDsl implements PrintableDelegate {

	Map<String, Map<String, String>> values
	Path input
	Optional<Path> output = Optional.empty()
	List<String> filters
	List<String> metrics
	List<String> jars

	DetectorConfigDsl(final Map<String, Map<String, String>> values = new HashMap<>(),
					  final Path input = null,
					  final Optional<Path> output = Optional.empty(),
					  final List<String> filters = new ArrayList<>(),
					  final List<String> jars = new ArrayList<>(),
					  final List<String> metrics = new ArrayList<>()) {
		this.values = values
		this.input = input
		this.output = output
		this.filters = filters
		this.metrics = metrics
		this.jars = jars
	}

	/**
	 * Should be called before using this object!
	 */
	void validate() {
		ValidateDsl.isTrue(Files.exists(input), "Input path $input does not exist!")
	}

	DetectorConfig build() {
		return new DetectorConfig(values)
	}

	void jars(List<String> paths) {
		jars = paths
	}

	void metrics(List<String> values) {
		metrics = values
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

	@Override
	String print(int indent) {
		def tabs = tabsForIndent(indent)
		def newIndent = indent + 1
		def newTabs = "${tabs}\t"

		def inputString = newTabs + "input '$input'"
		def outputString = output.map { "${tabs}\toutput '$it'\n" }.orElse(null) ?: ""

		def filterString = new FiltersDelegate(filters).print(newIndent)

		def metricsString = newTabs + "metrics(" + metrics.toString() + ")"
		def jarsString = newTabs + "jars(" + jars.toString() + ")"

		def detectorsString = "${tabs}\tdetectors {\n" +
				new DetectorsDelegate(values).print(newIndent + 1) +
				"\n${tabs}\t}"

		return "${tabs}config {\n\n" +
				inputString + "\n" +
				outputString + "\n" +
				filterString + "\n\n" +
				metricsString + "\n\n" +
				jarsString + "\n\n" +
				detectorsString + "\n" +
				"\n${tabs}}"
	}
}