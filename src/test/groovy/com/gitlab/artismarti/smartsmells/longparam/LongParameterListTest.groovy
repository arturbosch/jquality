package com.gitlab.artismarti.smartsmells.longparam

import com.gitlab.artismarti.smartsmells.common.SourceRange
import com.gitlab.artismarti.smartsmells.longmethod.LongMethod
import spock.lang.Specification

/**
 * @author artur
 */
class LongParameterListTest extends Specification {

	def "long param list can use fields of long method through delegation"() {
		expect:
		lpl.header == "header"
		lpl.name == "name"
		lpl.longMethod.name == "name"

		where:
		lpl = new LongParameterList(
				new LongMethod("header", "name", "signature", 1, 1, SourceRange.of(1, 1, 1, 1)),
				["1", "2"])
	}
}
