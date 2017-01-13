package io.gitlab.arturbosch.smartsmells.metrics

import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class MetricsTest extends Specification {

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

		where:
		clazz = Test.firstClass(Test.compile(Test.GOD_CLASS_DUMMY_PATH))
		tcc = Metrics.tcc(clazz)
	}


	def "sloc"() {
		when:
		def clazz = Test.firstClass(Test.compile(Test.COMMENT_DUMMY_PATH))
		int count = Metrics.sloc(clazz, Test.COMMENT_DUMMY_PATH)
		then:
		count == 6
	}


	def "loc"() {
		when:
		def clazz = Test.firstClass(Test.compile(Test.COMMENT_DUMMY_PATH))
		int count = Metrics.loc(clazz, Test.COMMENT_DUMMY_PATH)
		then:
		count == 18
	}

	def "cc"() {
		given:
		def resolver = new Resolver(JPAL.new(Test.PATH))
		when:
		def clazz = Test.firstClass(Test.compile(Test.GOD_CLASS_DUMMY_PATH))
		int cc = Metrics.cc(clazz, resolver)
		then:
		cc == 2
	}

	def "cm"() {
		given:
		def resolver = new Resolver(JPAL.new(Test.PATH))
		when:
		def clazz = Test.firstClass(Test.compile(Test.COMPLEX_METHOD_DUMMY_PATH))
		int cm = Metrics.cm(clazz, resolver)
		then:
		cm == 6
	}
}
