package io.gitlab.arturbosch.smartsmells.smells

import com.github.javaparser.ast.body.FieldDeclaration
import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
interface CycleSpecific extends FieldSpecific {
	String secondName()

	String secondSignature()

	CycleSpecific copyOnSecond(FieldDeclaration field)
}
