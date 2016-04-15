package com.gitlab.artismarti.smartsmells.complexmethod

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class ComplexMethodDetectorTest extends Specification {

	def "finds one complex method"() {
		expect:
		smells.size() == 1
		smells.get(0).cyclomaticComplexity == 11
		smells.get(0).longMethod.name == "complexMethod"

		where:
		smells = new ComplexMethodDetector().run(Test.PATH.resolve("com/gitlab/artismarti/smartsmells/java"))
	}
}
