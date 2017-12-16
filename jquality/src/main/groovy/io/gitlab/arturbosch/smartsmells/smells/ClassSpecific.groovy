package io.gitlab.arturbosch.smartsmells.smells

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
interface ClassSpecific extends NameAndSignatureSpecific {
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz)
}
