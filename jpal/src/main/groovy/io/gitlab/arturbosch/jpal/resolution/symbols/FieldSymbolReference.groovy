package io.gitlab.arturbosch.jpal.resolution.symbols

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.expr.SimpleName
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.resolution.QualifiedType

/**
 * @author Artur Bosch
 */
@EqualsAndHashCode(callSuper = true)
@ToString(includePackage = false, includeSuper = true, includeNames = false)
class FieldSymbolReference extends VariableSymbolReference implements WithPreviousSymbolReference {
	FieldDeclaration field

	FieldSymbolReference(SimpleName symbol, QualifiedType qualifiedType, FieldDeclaration declaration) {
		super(symbol, qualifiedType)
		field = declaration
	}

	@Override
	boolean isBuilderPattern() {
		return isBuilderPattern(qualifiedType)
	}
}
