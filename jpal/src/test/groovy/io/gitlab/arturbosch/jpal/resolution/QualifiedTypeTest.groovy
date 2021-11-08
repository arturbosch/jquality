package io.gitlab.arturbosch.jpal.resolution

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

	def "inner class with default package"() {
		given:
		def name = "<default>.Cycle.CycleTwo"
		def qualifiedType = new QualifiedType(name, QualifiedType.TypeToken.REFERENCE)
		when:
		def clazz = qualifiedType.asOuterClass()
		then:
		clazz.name == "<default>.Cycle"
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

	def "test additional methods on qualified type"() {
		given: "a inner class"
		QualifiedType cycleType = new QualifiedType("io.gitlab.arturbosch.jpal.CycleDummy.InnerCycleOne",
				QualifiedType.TypeToken.REFERENCE)

		when: "calling as outer class"
		def outerType = cycleType.asOuterClass()

		then: "it becomes an outer class type"
		outerType.name == "io.gitlab.arturbosch.jpal.CycleDummy"

		when: "retrieving path to its file"
		def javaFile = cycleType.asStringPathToJavaFile()

		then: "package structure until outer class is given as string"
		javaFile == "io/gitlab/arturbosch/jpal/CycleDummy.java"

		when: "calling short name"
		def shortName = cycleType.shortName()

		then: "only the inner class name is returned"
		shortName == "InnerCycleOne"

		when: "verifying type"
		then: "only the reference type is true"
		!cycleType.isFromJdk()
		!cycleType.isPrimitive()
		cycleType.isReference()
	}

	def "the short name of not a qualified outer type is just the class name"() {
		expect:
		type.shortName() == "CycleDummy"
		!type.isInnerClass()
		where:
		type = new QualifiedType("CycleDummy", QualifiedType.TypeToken.REFERENCE)
	}

}
