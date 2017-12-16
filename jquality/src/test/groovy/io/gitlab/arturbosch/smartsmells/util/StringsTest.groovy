package io.gitlab.arturbosch.smartsmells.util

import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class StringsTest extends Specification {

	def "levenshteins distance"() {
		when:
		def first = "absolutwodka"
		def second = "absolutwater"
		then:
		Strings.distance(first, second) == 4
	}
}
