package io.gitlab.arturbosch.jpal.resolution.symbols

import com.github.javaparser.ast.expr.SimpleName
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import io.gitlab.arturbosch.jpal.resolution.QualifiedType

/**
 * @author Artur Bosch
 */
@TupleConstructor
@ToString(includePackage = false, includeNames = false)
abstract class SymbolReference {
	SimpleName symbol
	QualifiedType qualifiedType

	boolean isType() {
		return this instanceof TypeSymbolReference
	}

	boolean isMethod() {
		return this instanceof MethodSymbolReference
	}

	boolean isVariable() {
		return this instanceof VariableSymbolReference
	}

	TypeSymbolReference asType() {
		return this as TypeSymbolReference
	}

	MethodSymbolReference asMethod() {
		return this as MethodSymbolReference
	}

	VariableSymbolReference asVariable() {
		return this as VariableSymbolReference
	}
}