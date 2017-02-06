package io.gitlab.arturbosch.smartsmells.smells.deadcode

import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.type.ClassOrInterfaceType
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class DeadCodeTest extends Specification {

	def "test"() {
		given:
		def deadCode = new DeadCode("hio", "String hio = \"hio\"", null, null, ElementTarget.FIELD)
		def fieldDeclaration = new FieldDeclaration(EnumSet.allOf(Modifier.class), NodeList.nodeList(
				new VariableDeclarator(new ClassOrInterfaceType("String"), "bye"),
				new VariableDeclarator(new ClassOrInterfaceType("String"), "bio"),
				new VariableDeclarator(new ClassOrInterfaceType("String"), "hao")
		))
		when:
		def copy = deadCode.copy(fieldDeclaration)
		then:
		copy.name() == "bio"
	}
}
