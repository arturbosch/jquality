package io.gitlab.arturbosch.smartsmells.smells.statechecking

import com.github.javaparser.ast.stmt.IfStmt
import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class StateCheckingTest extends Specification {

	def "copy"() {
		given:
		def smell = new StateCheckingDetector().run(Test.BASE_PATH.resolve("statecheck/StateCheck1.java"))[0]
		def ifStmt = Test.nth(Test.compile(Test.BASE_PATH.resolve("statecheck/StateCheck2.java")), 0).getChildNodesByType(IfStmt.class)[0]
		when:
		def copy = smell.copy(ifStmt)
		then:
		copy.signature() == "StateCheck2#public int replaceWithPolymorphism(Staty staty)#staty instanceof BigStaty, staty instanceof SmallStaty, staty instanceof StuffStaty, staty instanceof VerySmallStaty, staty instanceof VeryBigStaty"
	}

}
