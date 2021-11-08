package io.gitlab.arturbosch.jpal.resolution.symbols

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.nodeTypes.NodeWithType
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.resolution.QualifiedType

/**
 * @author Artur Bosch
 */
@CompileStatic
@EqualsAndHashCode(callSuper = true)
@ToString(includePackage = false, includeSuper = true, includeNames = false)
class NodeWithTypeSymbolReference extends SymbolReference {
	NodeWithType declaration

	NodeWithTypeSymbolReference(SimpleName symbol, QualifiedType qualifiedType, NodeWithType declaration) {
		super(symbol, qualifiedType)
		this.declaration = declaration
	}
}
