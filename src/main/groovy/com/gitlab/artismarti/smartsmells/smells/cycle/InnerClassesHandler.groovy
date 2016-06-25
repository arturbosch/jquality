package com.gitlab.artismarti.smartsmells.smells.cycle

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.type.Type
import com.gitlab.artismarti.smartsmells.common.helper.NodeHelper

/**
 * @author artur
 */
class InnerClassesHandler {

	private String outerClassName
	private Set<String> innerClassesNames

	InnerClassesHandler(CompilationUnit unit) {
		innerClassesNames = new HashSet<>()
		def types = unit.getTypes()
		if (types.size() >= 1) {
			def mainClass = types[0]
			innerClassesNames = NodeHelper.findNamesOfInnerClasses(mainClass)
			outerClassName = mainClass.name
		} else {
			throw new NoClassesException()
		}
	}

	private boolean isInnerClass(String className) {
		return innerClassesNames.contains(className)
	}

	static String appendOuterClassIfInnerClass(ClassOrInterfaceDeclaration n) {
		def unqualifiedName = n.name
		if (n.parentNode instanceof ClassOrInterfaceDeclaration) {
			def parentName = ((ClassOrInterfaceDeclaration) n.parentNode).name
			unqualifiedName = "$parentName.$n.name"
		}
		unqualifiedName
	}

	String getUnqualifiedNameForInnerClass(Type type) {
		isInnerClass(type.toString()) ? "${outerClassName}.$type" : "$type"
	}

}
