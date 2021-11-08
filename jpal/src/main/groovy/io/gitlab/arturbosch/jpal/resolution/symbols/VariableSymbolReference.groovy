package io.gitlab.arturbosch.jpal.resolution.symbols

import com.github.javaparser.ast.expr.SimpleName
import io.gitlab.arturbosch.jpal.resolution.QualifiedType

/**
 * @author Artur Bosch
 */
abstract class VariableSymbolReference extends SymbolReference {

	VariableSymbolReference(SimpleName symbol, QualifiedType qualifiedType) {
		super(symbol, qualifiedType)
	}

	boolean isLocaleVariable() {
		return this instanceof LocaleVariableSymbolReference
	}

	boolean isParameter() {
		return this instanceof ParameterSymbolReference
	}

	boolean isField() {
		return this instanceof FieldSymbolReference
	}

	LocaleVariableSymbolReference asLocaleVariable() {
		return this as LocaleVariableSymbolReference
	}

	ParameterSymbolReference asParameter() {
		return this as ParameterSymbolReference
	}

	FieldSymbolReference asField() {
		return this as FieldSymbolReference
	}

}