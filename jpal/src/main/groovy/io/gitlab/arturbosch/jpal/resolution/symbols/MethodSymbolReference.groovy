package io.gitlab.arturbosch.jpal.resolution.symbols

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.SimpleName
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.resolution.QualifiedType

/**
 * @author Artur Bosch
 */
@EqualsAndHashCode(callSuper = true)
@ToString(includePackage = false, includeSuper = true, includeNames = false)
class MethodSymbolReference extends SymbolReference implements WithPreviousSymbolReference {
	MethodDeclaration declaration

	MethodSymbolReference(SimpleName symbol, QualifiedType qualifiedType, MethodDeclaration declaration) {
		super(symbol, qualifiedType)
		this.declaration = declaration
	}

	@Override
	boolean isBuilderPattern() {
		return isBuilderPattern(qualifiedType)
	}
}
