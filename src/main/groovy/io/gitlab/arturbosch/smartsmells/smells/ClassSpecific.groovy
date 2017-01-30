package io.gitlab.arturbosch.smartsmells.smells;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

/**
 * @author Artur Bosch
 */
interface ClassSpecific {
	ClassSpecific copy(ClassOrInterfaceDeclaration clazz)
}
