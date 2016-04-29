package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
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

}
