package com.gitlab.artismarti.smartsmells.common

import spock.lang.Specification

/**
 * @author artur
 */
class CompilationStorageTest extends Specification {

	private QualifiedType cycleType = new QualifiedType("com.gitlab.artismarti.smartsmells.java.CycleDummy",
			QualifiedType.TypeToken.REFERENCE)

	private QualifiedType innerCycleType = new QualifiedType(cycleType.name + ".InnerCycleOne",
			QualifiedType.TypeToken.REFERENCE)

	def "domain tests"() {
		given:
		CompilationStorage.create(Test.PATH)

		when: "retrieving all compilation info"
		def info = CompilationStorage.allCompilationInfo

		then: "its size must be greater than 10 because the java package has more than 10 dummies)"
		info.size() > 10

		when: "retrieving a specific type (cycle)"
		def cycleInfo = CompilationStorage.getCompilationInfo(cycleType).get()

		then: "it should have 2 inner classes"
		cycleInfo.innerClasses.size() == 2

		when: "retrieving info for a inner class"
		def infoFromInnerClass = CompilationStorage.getCompilationInfo(innerCycleType).get()

		then: "it should return info of outer class"
		infoFromInnerClass.qualifiedType == cycleType

	}
}
