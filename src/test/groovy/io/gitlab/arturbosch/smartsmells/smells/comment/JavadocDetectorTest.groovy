package io.gitlab.arturbosch.smartsmells.smells.comment

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class JavadocDetectorTest extends Specification {

	def "check absence of javadoc"() {
		when:
		def smells = new JavadocDetector().run(Test.COMMENT_DUMMY_PATH)
		then:
		smells.size() == 3
	}
}
