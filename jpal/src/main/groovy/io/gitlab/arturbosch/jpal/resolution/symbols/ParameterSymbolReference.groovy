package io.gitlab.arturbosch.jpal.resolution.symbols

import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.SimpleName
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.resolution.QualifiedType

/**
 * @author Artur Bosch
 */
@EqualsAndHashCode(callSuper = true)
@ToString(includePackage = false, includeSuper = true, includeNames = false)
class ParameterSymbolReference extends VariableSymbolReference {
	Parameter parameter

	ParameterSymbolReference(SimpleName symbol, QualifiedType qualifiedType, Parameter declaration) {
		super(symbol, qualifiedType)
		parameter = declaration
	}
}
