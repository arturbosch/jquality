package io.gitlab.arturbosch.smartsmells.config.dsl

import io.gitlab.arturbosch.smartsmells.config.DetectorInitializer
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethodDetector
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class DetectorConfigDslRunnerTest extends Specification {

	def "run script"() {
		when:
		def runner = new DetectorConfigDslRunner()
		def config = runner.execute(new File(getClass().getResource("/dsl/config.groovy").path))
		then:
		config.input.toString() == "/path/to/project"
		config.output.get().toString() == "/path/to/output"
		when:
		def detectors = DetectorInitializer.init(config.build())
		then:
		detectors.size() == 1
		detectors[0] instanceof LongMethodDetector
	}

	def "convert to script"() {
		given: "a dsl object"
		def map = ["Hello": "World", "World": "Hello"]
		def filters = new FiltersDelegate([".*/test/.*", ".*/testData/.*"])
		def detectors = new DetectorsDelegate(["longmethod": map, "largeclass": map])
		def dslObject = new DetectorConfigDsl(detectors.values,
				Paths.get("/hello/world"),
				Optional.of(Paths.get("/world/hello")),
				filters.filters)
		when: "convereting dsl object to dsl string and back"
		def dsl = dslObject.print(0)
		def dslObjectTransformed = DetectorConfigDslRunner.execute(dsl)
		then: "both objects should be equals"
		dslObject == dslObjectTransformed

	}
}
