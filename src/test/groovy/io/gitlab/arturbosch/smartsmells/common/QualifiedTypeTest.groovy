package io.gitlab.arturbosch.smartsmells.common

import spock.lang.Specification

/**
 * @author artur
 */
class QualifiedTypeTest extends Specification {

	def "qualified type matches type of inner classes"() {
		given:
		def name = "com.gitlab.artismarti.smartsmells.java.CycleDummy.InnerCycleOne"
		when:
		def qualifiedType = new QualifiedType(name, QualifiedType.TypeToken.REFERENCE)
		then:
		qualifiedType.isInnerClass()
	}

	def "invalid type is no inner class"() {
		given:
		def name = ".CycleDummy"
		when:
		def qualifiedType = new QualifiedType(name, QualifiedType.TypeToken.REFERENCE)
		then:
		!qualifiedType.isInnerClass()
	}

	def "from inner class type to outer class type"() {

		given: "types of outer class and its inner class"
		def name = "com.gitlab.artismarti.smartsmells.java.CycleDummy.InnerCycleOne"
		def outerType = new QualifiedType("com.gitlab.artismarti.smartsmells.java.CycleDummy",
				QualifiedType.TypeToken.REFERENCE)
		when: "when calling inner class as outer class"
		def qualifiedType = new QualifiedType(name, QualifiedType.TypeToken.REFERENCE)
		def outerClass = qualifiedType.asOuterClass()

		then: "returned class should be outer class"
		outerType == outerClass
	}
}
