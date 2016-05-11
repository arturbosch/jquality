package com.gitlab.artismarti.smartsmells

import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.common.Test
import com.gitlab.artismarti.smartsmells.complexmethod.ComplexMethodDetector
import com.gitlab.artismarti.smartsmells.longmethod.LongMethodDetector
import com.gitlab.artismarti.smartsmells.longparam.LongParameterListDetector
import spock.lang.Specification

/**
 * @author artur
 */
class DetectorFacadeTest extends Specification {


	def "build facade with three detectors"() {
		expect:
		facade.numberOfDetectors() == 3

		where:
		facade = DetectorFacade.builder()
				.with(new LongParameterListDetector())
				.with(new LongMethodDetector())
				.with(new ComplexMethodDetector())
				.build()
	}

	def "run facade and get result set of long methods"() {
		expect:
		!result.of(Smell.LONG_METHOD).isEmpty()

		where:
		result = DetectorFacade.builder().fullStackFacade().run(Test.PATH)
	}
}
