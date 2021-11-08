package io.gitlab.arturbosch.jpal.ast

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import io.gitlab.arturbosch.jpal.Helper
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class VariableHelperTest extends Specification {

	def unit = Helper.compile(Helper.DUMMY)

	def "fields to jpal variables"() {
		given:
		def fields = unit.getChildNodesByType(FieldDeclaration.class)
		def params = unit.getChildNodesByType(Parameter.class)
		def locales = unit.getChildNodesByType(VariableDeclarationExpr.class)
		when:
		def jpalVars = VariableHelper.toJpalFromFields(fields)
		def jpalVars2 = VariableHelper.toJpalFromParameters(params)
		def jpalVars3 = VariableHelper.toJpalFromLocales(locales)
		then:
		jpalVars.size() >= 3
		jpalVars2.size() >= 2
		jpalVars3.size() >= 3
	}
}
