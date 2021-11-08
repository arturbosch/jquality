package io.gitlab.arturbosch.jpal.resolution.symbols

import com.github.javaparser.ast.body.TypeDeclaration
import com.github.javaparser.ast.expr.SimpleName
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.resolution.QualifiedType

/**
 * @author Artur Bosch
 */
@EqualsAndHashCode(callSuper = true)
@ToString(includePackage = false, includeSuper = true, includeNames = false)
class TypeSymbolReference extends SymbolReference {
	TypeDeclaration declaration

	TypeSymbolReference(SimpleName symbol, QualifiedType qualifiedType, TypeDeclaration declaration) {
		super(symbol, qualifiedType)
		this.declaration = declaration
	}

}
