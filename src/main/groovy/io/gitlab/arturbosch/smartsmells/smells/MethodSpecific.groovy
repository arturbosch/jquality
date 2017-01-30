package io.gitlab.arturbosch.smartsmells.smells

import com.github.javaparser.ast.body.MethodDeclaration

/**
 * @author Artur Bosch
 */
interface MethodSpecific extends NameAndSignatureSpecific {
	MethodSpecific copy(MethodDeclaration method)
}
