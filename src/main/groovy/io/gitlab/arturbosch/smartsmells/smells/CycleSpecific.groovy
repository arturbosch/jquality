package io.gitlab.arturbosch.smartsmells.smells

import com.github.javaparser.ast.body.FieldDeclaration

/**
 * @author Artur Bosch
 */
interface CycleSpecific extends FieldSpecific {
	String secondName()

	String secondSignature()

	CycleSpecific copyOnSecond(FieldDeclaration field)
}