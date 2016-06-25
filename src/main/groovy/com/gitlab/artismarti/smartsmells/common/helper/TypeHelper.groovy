package com.gitlab.artismarti.smartsmells.common.helper

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.ReferenceType
import com.github.javaparser.ast.type.Type
import com.gitlab.artismarti.smartsmells.common.PackageImportHolder
import com.gitlab.artismarti.smartsmells.common.QualifiedType
import com.gitlab.artismarti.smartsmells.smells.cycle.NoClassesException

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
			return Optional.of(getQualifiedType(n, maybeUnit.get()))
		}
		return Optional.empty()
	}

	static QualifiedType getQualifiedType(ClassOrInterfaceDeclaration n, CompilationUnit unit) {
		def name = n.name
		def holder = new PackageImportHolder(unit.package, unit.imports)
		PackageImportHelper.getQualifiedType(holder, new ClassOrInterfaceType(name))
	}

	static QualifiedType getQualifiedType(ClassOrInterfaceDeclaration n, PackageDeclaration declaration) {
		new QualifiedType("$declaration.packageName.$n.name", QualifiedType.TypeToken.REFERENCE)
	}

	static Set<QualifiedType> getQualifiedTypesOfInnerClasses(CompilationUnit unit) {
		def types = unit.getTypes()
		if (types.size() >= 1) {
			def mainClass = types[0]
			String packageName = unit?.package?.packageName ?: ""
			Set<String> innerClassesNames = NodeHelper.findNamesOfInnerClasses(mainClass)
			String outerClassName = mainClass.name
			return innerClassesNames.collect {
				new QualifiedType("$packageName.$outerClassName.$it", QualifiedType.TypeToken.REFERENCE)
			}
		} else {
			throw new NoClassesException()
		}
	}

}
