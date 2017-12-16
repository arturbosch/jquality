package io.gitlab.arturbosch.smartsmells.smells.cycle

import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.type.ClassOrInterfaceType
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class CycleTest extends Specification {

	def "copy test"() {
		given:
		def cycle = new Cycle(
				new Dependency("Source", "SourceSignature", null, null),
				new Dependency("Target", "TargetSignature", null, null)
		)

		def field = new FieldDeclaration(EnumSet.of(Modifier.PUBLIC),
				new VariableDeclarator(new ClassOrInterfaceType("NewSource"), "source",
						new ObjectCreationExpr(null, new ClassOrInterfaceType("NewSource"), NodeList.nodeList())))
		when:
		def copy = cycle.copy(field)
		def copySecond = cycle.copyOnSecond(field)
		then:
		copy.name() == "NewSource"
		copy.signature() == "public NewSource source = new NewSource();"
		copySecond.secondName() == "NewSource"
		copySecond.secondSignature() == "public NewSource source = new NewSource();"
	}

}
