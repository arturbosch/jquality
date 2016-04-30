package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ast.body.VariableDeclaratorId
import com.github.javaparser.ast.expr.AnnotationExpr
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.type.Type
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import groovy.transform.ToString

/**
 * @author artur
 */
@ToString(includePackage = false)
class CustomVariableDeclaration {

	int modifiers;
	List<AnnotationExpr> annotations;
	Type type;
	String name;
	int arrayCount;
	Expression expression;
	SourceRange sourceRange

	public CustomVariableDeclaration(final int beginLine, final int beginColumn, final int endLine, final int endColumn,
	                                 final int modifiers, final List<AnnotationExpr> annotations, final Type type,
	                                 final VariableDeclaratorId id, final Expression expression) {
		this.sourceRange = SourceRange.of(beginLine, endLine, beginColumn, endColumn)
		this.modifiers = modifiers
		this.annotations = annotations
		this.type = type
		this.name = id.name
		this.arrayCount = id.arrayCount
		this.expression = expression
	}

}
