package com.gitlab.artismarti.smartsmells.common

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
}
