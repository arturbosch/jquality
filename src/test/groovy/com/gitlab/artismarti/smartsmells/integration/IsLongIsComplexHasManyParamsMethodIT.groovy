package com.gitlab.artismarti.smartsmells.integration

import com.gitlab.artismarti.smartsmells.smells.complexmethod.ComplexMethod
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.smells.longmethod.LongMethod
import com.gitlab.artismarti.smartsmells.smells.longparam.LongParameterList
import com.gitlab.artismarti.smartsmells.start.DetectorFacade
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author artur
 */
class IsLongIsComplexHasManyParamsMethodIT extends Specification {

	def "complex, long and long parameter methods have same signature"() {
		given:
		def path = Paths.get(getClass().getResource("/invalid/GodClass.java").getFile())

		when:
		def result = DetectorFacade.builder().fullStackFacade().run(path)
		def lm = ((LongMethod) result.of(Smell.LONG_METHOD).getFirst()).signature
		def cm = ((ComplexMethod) result.of(Smell.COMPLEX_METHOD).getFirst()).signature
		def lpl = ((LongParameterList) result.of(Smell.LONG_PARAM).getFirst()).signature

		then:
		result.of(Smell.COMPLEX_METHOD).size() == 1
		result.of(Smell.LONG_METHOD).size() == 1
		result.of(Smell.LONG_PARAM).size() == 1
		lm == cm
		cm == lpl
		lm == lpl

	}
}
