package io.gitlab.arturbosch.jpal.ast

import com.github.javaparser.ast.body.MethodDeclaration
import io.gitlab.arturbosch.jpal.Helper
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class MethodHelperTest extends Specification {

	private List<MethodDeclaration> methodsWithinUnit =
			Helper.compile(Helper.DUMMY).getChildNodesByType(MethodDeclaration.class)

	def "extract parameters from body declarations"() {
		given:
		def method = methodsWithinUnit[1]
		when:
		def parameters = MethodHelper.extractParameters(method)
		then:
		parameters.size() >= 2
	}

	def "is getter and setter"() {
		given:
		def methods = methodsWithinUnit
		def setter = methods[3]
		def getter = methods[2]
		when:
		def isSetter = MethodHelper.isGetterOrSetter(setter)
		def isGetter = MethodHelper.isGetterOrSetter(getter)
		then:
		isSetter
		isGetter
	}

	def "count method invocations within a method"() {
		given:
		def method = methodsWithinUnit[0]
		when:
		def all = MethodHelper.getAllMethodInvocations(method)
		def single = MethodHelper.getAllMethodInvocationsForEntityWithName("strings", method)
		then:
		all == 1
		single == 1
	}

	def "filter anonymous methods"() {
		given:
		def methods = Helper.compile(Helper.ANONYMOUS_DUMMY).getChildNodesByType(MethodDeclaration.class)
		when:
		def filteredMethods = MethodHelper.filterAnonymousMethods(methods)
		then:
		filteredMethods.size() == 2
	}

	def "size bigger than two"() {
		given:
		def method = methodsWithinUnit[0]
		when:
		def isBigger = MethodHelper.sizeBiggerThan(2, method)
		then:
		isBigger
	}
}
