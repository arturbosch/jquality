package com.gitlab.artismarti.smartsmells.common.helper

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.ReferenceType
import com.github.javaparser.ast.type.Type

/**
 * @author artur
 */
class TypeHelper {

	static boolean isEmptyBody(ClassOrInterfaceDeclaration n) {
		n.members.empty
	}

	static boolean hasNoMethods(ClassOrInterfaceDeclaration n) {
		n.members.stream().filter { it instanceof MethodDeclaration }.count() == 0
	}

	@SuppressWarnings("GroovyAssignabilityCheck")
	static Optional<ClassOrInterfaceType> getClassOrInterfaceType(Type type) {
		Type tmp = type
		while (tmp instanceof ReferenceType) {
			tmp = ((ReferenceType) tmp).getType()
		}
		if (tmp instanceof ClassOrInterfaceType) {
			return Optional.of((ClassOrInterfaceType) tmp)
		}
		return Optional.empty()
	}

}
