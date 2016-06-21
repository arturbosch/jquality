package com.gitlab.artismarti.smartsmells.common.helper

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification
/**
 * @author artur
 */
class MetricHelperTest extends Specification {

	def "mccabe"() {
		expect:
		mcc == 10

		where:
		method = Test.nth(Test.compile(Test.GOD_CLASS_DUMMY_PATH), 3)
		mcc = MetricHelper.mcCabe(method)
	}

	def "wmc"() {
		expect:
		wmc == 22

		where:
		clazz = Test.firstClass(Test.compile(Test.GOD_CLASS_DUMMY_PATH))
		wmc = MetricHelper.wmc(clazz)
	}
}
