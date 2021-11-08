package io.gitlab.arturbosch.jpal.core

import io.gitlab.arturbosch.jpal.Helper
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class TwoClassesSameFileTest extends Specification {

	def "should store two qualified types for two classes in one file"() {
		given:
		def path = Helper.BASE_PATH.resolve("A.java")
		def storage = JPAL.newInstance(path)

		when:
		def infos = storage.allQualifiedTypes

		then:
		infos.size() == 2
	}
}
