package io.gitlab.arturbosch.smartsmells.common

import com.github.javaparser.ast.body.VariableDeclaratorId
import com.github.javaparser.ast.expr.AnnotationExpr
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.type.Type
import groovy.transform.ToString
import io.gitlab.arturbosch.smartsmells.common.source.SourceRange

/**
 * @author artur
 */
@ToString(includePackage = false)
class CustomVariableDeclaration {

	enum Nature {
		Local, Field, Parameter
	}

	int modifiers;
	List<AnnotationExpr> annotations;
	Type type;
	String name;
	int arrayCount;
	Expression expression;
	SourceRange sourceRange
	Nature nature


	public CustomVariableDeclaration(final int beginLine, final int beginColumn, final int endLine, final int endColumn,
	                                 final int modifiers, final List<AnnotationExpr> annotations, final Type type,
	                                 final VariableDeclaratorId id, final Expression expression, final Nature nature) {
		this.sourceRange = SourceRange.of(beginLine, endLine, beginColumn, endColumn)
		this.modifiers = modifiers
		this.annotations = annotations
		this.type = type
		this.name = id.name
		this.arrayCount = id.arrayCount
		this.expression = expression
		this.nature = nature
	}

}
