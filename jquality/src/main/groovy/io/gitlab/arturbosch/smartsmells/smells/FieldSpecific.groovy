package io.gitlab.arturbosch.smartsmells.smells

import com.github.javaparser.ast.body.FieldDeclaration
import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
interface FieldSpecific extends NameAndSignatureSpecific {
	FieldSpecific copy(FieldDeclaration field)
}
