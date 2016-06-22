package com.gitlab.artismarti.smartsmells.common.helper

import com.gitlab.artismarti.smartsmells.common.Test
import com.gitlab.artismarti.smartsmells.metrics.Metrics
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
		mcc = Metrics.mcCabe(method)
	}

	def "wmc"() {
		expect:
		wmc == 22

		where:
		clazz = Test.firstClass(Test.compile(Test.GOD_CLASS_DUMMY_PATH))
		wmc = Metrics.wmc(clazz)
	}

	def "noa"() {
		expect: "5 without inner classes"
		noa == 5

		where:
		clazz = Test.firstClass(Test.compile(Test.FEATURE_ENVY_PATH))
		noa = Metrics.noa(clazz)
	}

	def "nom"() {
		expect: "2 without inner classes"
		nom == 2

		where:
		clazz = Test.firstClass(Test.compile(Test.FEATURE_ENVY_PATH))
		nom = Metrics.nom(clazz)
	}

	def "atfd"() {
		expect: "uses ComplexDummy"
		atfd == 5

		where:
		clazz = Test.firstClass(Test.compile(Test.GOD_CLASS_DUMMY_PATH))
		atfd = Metrics.atfd(clazz)
	}

	def "tcc"() {
		expect:
		tcc == 0.7d
		println tcc

		where:
		clazz = Test.firstClass(Test.compile(Test.GOD_CLASS_DUMMY_PATH))
		tcc = Metrics.tcc(clazz)
	}


	def "sloc"() {
		when:
		int count = Metrics.sloc(Test.COMMENT_DUMMY_PATH)
		then:
		count == 6
	}


	def "loc"() {
		when:
		int count = Metrics.loc(Test.COMMENT_DUMMY_PATH)
		then:
		count == 18
	}
}
