package io.gitlab.arturbosch.smartsmells.smells

import com.github.javaparser.ast.body.FieldDeclaration

/**
 * @author Artur Bosch
 */
interface FieldSpecific extends NameAndSignatureSpecific {
	FieldSpecific copy(FieldDeclaration field)
}
