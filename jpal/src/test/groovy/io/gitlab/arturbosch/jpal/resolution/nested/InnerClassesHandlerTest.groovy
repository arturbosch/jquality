package io.gitlab.arturbosch.jpal.resolution.nested

import com.github.javaparser.ast.type.ClassOrInterfaceType
import io.gitlab.arturbosch.jpal.Helper
import spock.lang.Specification

/**
 * @author artur
 */
class InnerClassesHandlerTest extends Specification {

	def "domain tests"() {
		given: "inner classes handler"
		def handler = new InnerClassesHandler(Helper.compile(Helper.CYCLE_DUMMY))
		when: "asking for the composite name of outer class and inner class - InnerCycleOne"
		def name = handler.getUnqualifiedNameForInnerClass(new ClassOrInterfaceType("InnerCycleOne"))
		then: "the right name is returned"
		name == "CycleDummy.InnerCycleOne"
	}

	def "No classes expection on empty CU"() {
		when:
		def unit = Helper.compile(Helper.NO_CONTENT_DUMMY)
		//noinspection GroovyResultOfObjectAllocationIgnored
		new InnerClassesHandler(unit)
		then:
		thrown(NoClassesException.class)
	}

}
