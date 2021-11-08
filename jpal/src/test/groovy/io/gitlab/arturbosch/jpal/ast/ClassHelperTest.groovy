package io.gitlab.arturbosch.jpal.ast

import io.gitlab.arturbosch.jpal.Helper
import spock.lang.Specification

/**
 * @author artur
 */
class ClassHelperTest extends Specification {

	def "class is empty and has no methods"() {
		expect:
		ClassHelper.hasNoMethods(aClass)
		ClassHelper.isEmptyBody(aClass)
		where:
		aClass = Helper.firstClass(Helper.compile(Helper.EMPTY_DUMMY))
	}

	def "method is within class scope"() {
		expect:
		ClassHelper.inClassScope(aMethod, aClass.nameAsString)
		where:
		unit = Helper.compile(Helper.CYCLE_DUMMY)
		aClass = Helper.firstClass(unit)
		aMethod = Helper.nth(unit, 0)
	}

	def "default vs full signature"() {
		given: "cycle class with two inner classes"
		def innerClass = Helper.nthInnerClass(Helper.compile(Helper.CYCLE_DUMMY), 0)
		when: "calling signature methods"
		def signature = ClassHelper.createSignature(innerClass)
		def fullSignature = ClassHelper.createFullSignature(innerClass)
		then: "full signature embraces the inner class behaviour"
		signature == "InnerCycleOne"
		fullSignature == "CycleDummy\$InnerCycleOne"

		when: "requesting the unqualified signature of the inner class"
		def unqualifiedName = ClassHelper.appendOuterClassIfInnerClass(innerClass)
		then: "the outer class is appended"
		unqualifiedName == "CycleDummy.InnerCycleOne"
	}

	def "full signature of a complex type has proper spacing"() {
		given: "dummy class with complex inner class"
		def clazz = Helper.nthInnerClass(Helper.compile(Helper.BASE_PATH.resolve("ClassSignatureDummy.java")), 0)
		when: "retrieving the full signature"
		def fullSignature = ClassHelper.createFullSignature(clazz)
		then: "there must be a space between generic declarations"
		fullSignature == "ClassSignatureDummy\$VeryComplexInnerClass<T extends String, B extends List<T>> extends ClassSignatureDummy implements Cloneable, Runnable"
	}
}
