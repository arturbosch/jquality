package io.gitlab.arturbosch.smartsmells.smells

import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
interface MethodSpecific extends NameAndSignatureSpecific {
	MethodSpecific copy(MethodDeclaration method)
}
