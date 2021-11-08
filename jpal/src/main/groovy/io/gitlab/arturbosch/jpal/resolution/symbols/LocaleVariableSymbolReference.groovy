package io.gitlab.arturbosch.jpal.resolution.symbols

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.resolution.QualifiedType

/**
 * @author Artur Bosch
 */
@EqualsAndHashCode(callSuper = true)
@ToString(includePackage = false, includeSuper = true, includeNames = false)
class LocaleVariableSymbolReference extends VariableSymbolReference {
	VariableDeclarationExpr variable

	LocaleVariableSymbolReference(SimpleName symbol, QualifiedType qualifiedType, VariableDeclarationExpr declaration) {
		super(symbol, qualifiedType)
		variable = declaration
	}
}
