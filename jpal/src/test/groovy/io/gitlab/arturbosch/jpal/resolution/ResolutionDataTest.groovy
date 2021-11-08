package io.gitlab.arturbosch.jpal.resolution

import io.gitlab.arturbosch.jpal.Helper
import io.gitlab.arturbosch.jpal.resolution.ResolutionData
import spock.lang.Specification

/**
 * @author artur
 */
class ResolutionDataTest extends Specification {

	def "right amount of imports"() {
		given: "a compilation unit"
		def unit = Helper.compile(Helper.DUMMY)
		when: "resolution set for unit is requested"
		def data = ResolutionData.of(unit)
		then: "four imports are found"
		data.imports.size() == 4
	}
}
