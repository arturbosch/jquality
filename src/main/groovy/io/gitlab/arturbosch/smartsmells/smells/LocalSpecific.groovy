package io.gitlab.arturbosch.smartsmells.smells

import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.Statement

/**
 * @author Artur Bosch
 */
interface LocalSpecific extends NameAndSignatureSpecific {
	LocalSpecific copy(Statement statement)

	LocalSpecific copy(Expression expression)
}
