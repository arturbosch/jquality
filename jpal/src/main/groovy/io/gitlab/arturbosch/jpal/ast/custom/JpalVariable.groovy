package io.gitlab.arturbosch.jpal.ast.custom

import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.expr.AnnotationExpr
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.type.Type
import groovy.transform.CompileStatic
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourceRange

/**
 * Jpal variable based on {@code VariableDeclarationExpr} but with the difference
 * that every declared variable is represented as one jpal variable e.g. no more returning
 * of list of {@code VariableDeclarator}'s which is most time cumbersome to work with.
 *
 * This class is better constructed from a VariableDeclarationExpr, FieldDeclaration or Parameter
 * through the {@code VariableHelper} class.
 *
 * @author artur
 */
@ToString(includePackage = false)
@CompileStatic
class JpalVariable {

	/**
	 * Nature of jpal variable.
	 */
	@CompileStatic
	enum Nature {
		Local, Field, Parameter
	}

	final EnumSet<Modifier> modifiers
	final NodeList<AnnotationExpr> annotations
	final Type type
	final String name
	final int arrayCount
	final Optional<Expression> initExpression
	final SourceRange sourceRange
	final Nature nature

	JpalVariable(final int beginLine, final int beginColumn, final int endLine, final int endColumn,
				 final EnumSet<Modifier> modifiers, final NodeList<AnnotationExpr> annotations,
				 final Type type, final String name, final Optional<Expression> initExpression,
				 final Nature nature) {
		this.sourceRange = SourceRange.of(beginLine, endLine, beginColumn, endColumn)
		this.modifiers = modifiers
		this.annotations = annotations
		this.type = type
		this.name = name
		this.arrayCount = type.arrayLevel
		this.initExpression = initExpression
		this.nature = nature
	}

}
