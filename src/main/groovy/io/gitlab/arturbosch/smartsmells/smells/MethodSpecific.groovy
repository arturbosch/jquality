package io.gitlab.arturbosch.smartsmells.smells

import com.github.javaparser.ast.body.MethodDeclaration

/**
 * @author Artur Bosch
 */
interface MethodSpecific {
	MethodSpecific copy(MethodDeclaration method)
}
