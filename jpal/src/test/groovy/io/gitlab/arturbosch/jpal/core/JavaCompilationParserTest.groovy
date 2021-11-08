package io.gitlab.arturbosch.jpal.core

import io.gitlab.arturbosch.jpal.Helper
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class JavaCompilationParserTest extends Specification {

	def "compile parser test"() {
		given:
		def parser = new JavaCompilationParser()
		when:
		def dummy = parser.compile(Helper.DUMMY)
		then:
		dummy.isPresent()
	}
}
