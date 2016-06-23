package com.gitlab.artismarti.smartsmells.common.helper

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.ReferenceType
import com.github.javaparser.ast.type.Type
import com.gitlab.artismarti.smartsmells.common.PackageImportHolder
import com.gitlab.artismarti.smartsmells.common.QualifiedType

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

	static Optional<QualifiedType> getQualifiedType(ClassOrInterfaceDeclaration n) {
		def maybeUnit = NodeHelper.findDeclaringCompilationUnit(n)

		if (maybeUnit.isPresent()) {
			def name = n.name
			def unit = maybeUnit.get()
			def holder = new PackageImportHolder(unit.package, unit.imports)
			def qualifiedType = PackageImportHelper.getQualifiedType(
					holder, new ClassOrInterfaceType(name))
			return Optional.of(qualifiedType)
		}
		return Optional.empty()
	}
}
