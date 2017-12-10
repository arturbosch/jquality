package io.gitlab.arturbosch.smartsmells.metrics.internal

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class LinesOfCodeTest extends Specification {

	def "sloc"() {
		when:
		def clazz = Test.firstClass(Test.compile(Test.COMMENT_DUMMY_PATH))
		println(clazz.toString())
		def metrics = new LOC().raise(clazz)

		then:
		metrics.find { it.type == LOC.SLOC }.value == 10
		metrics.find { it.type == LOC.CLOC }.value == 13
		metrics.find { it.type == LOC.BLOC }.value == 2
		metrics.find { it.type == LOC.LLOC }.value == 7
		metrics.find { it.type == LOC.LOC }.value == 25
	}
}
