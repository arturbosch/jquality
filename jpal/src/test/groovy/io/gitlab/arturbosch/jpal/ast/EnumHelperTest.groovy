package io.gitlab.arturbosch.jpal.ast

import com.github.javaparser.ast.body.EnumDeclaration
import io.gitlab.arturbosch.jpal.Helper
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class EnumHelperTest extends Specification {

	def "create signature for enums"() {
		when:
		def enumDecl = Helper.compile(Helper.BASE_PATH.resolve("ClassSignatureDummy.java")).getChildNodesByType(EnumDeclaration.class)[0]
		def signature = EnumHelper.createSignature(enumDecl)
		def fullSignature = EnumHelper.createFullSignature(enumDecl)

		then:
		signature == "EnumSignatureDummy implements Runnable"
		fullSignature == "ClassSignatureDummy\$VeryComplexInnerClass<T extends String, B extends List<T>> extends " +
				"ClassSignatureDummy implements Cloneable, Runnable\$EnumSignatureDummy implements Runnable"
	}
}
