package io.gitlab.arturbosch.smartsmells.config.dsl

import io.gitlab.arturbosch.smartsmells.config.DetectorInitializer
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethodDetector
import spock.lang.Specification

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
}
