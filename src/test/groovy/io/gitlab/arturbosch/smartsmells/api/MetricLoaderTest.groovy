package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class MetricLoaderTest extends Specification {

	def "load extra metric from service entry"() {
		when: "loading extra jar"
		def paths = [Paths.get(getClass().getResource("/detector.jar").path)]
		def facade = MetricFacade.builder()
				.withLoader(new MetricLoader(new JarLoader(paths)))
				.build()
		then: "one 'Template' metric for CommentDummy is found"
		facade.run(Test.COMMENT_DUMMY_PATH)*.metrics.size() == 1
		facade.run(Test.COMMENT_DUMMY_PATH)[0].getMetric('Template').type == "Template"
	}
}
