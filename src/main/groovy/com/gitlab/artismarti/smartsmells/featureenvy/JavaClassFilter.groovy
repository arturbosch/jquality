package com.gitlab.artismarti.smartsmells.featureenvy

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.PrimitiveType
import com.github.javaparser.ast.type.ReferenceType
import com.gitlab.artismarti.smartsmells.common.CustomVariableDeclaration

import static java.util.stream.Collectors.toSet

/**
 * @author artur
 */
class JavaClassFilter {

	Map<String, String> imports

	JavaClassFilter(List<ImportDeclaration> imports) {
		this.imports = imports.collectEntries {
			[Arrays.asList(it.toStringWithoutComments().split("\\.")).last(), it.name.toStringWithoutComments()]
		}
	}

	public Set<CustomVariableDeclaration> forJavaClasses(Set<CustomVariableDeclaration> fields) {
		return fields.stream().filter { isNoPrimitiveType(it) }
				.filter { !isJavaType(it) }.collect(toSet());
	}

	private static boolean isNoPrimitiveType(CustomVariableDeclaration field) {
		return !(field.getType() instanceof PrimitiveType);
	}

	private boolean isJavaType(CustomVariableDeclaration field) {
		def typeOfVar = field.type.toStringWithoutComments()
		if (isJavaObject(typeOfVar)) {
			return true
		}

		if (field.getType() instanceof ReferenceType) {
			def type = (ReferenceType) field.getType()
			if (type.type instanceof ClassOrInterfaceType) {
				def realType = (ClassOrInterfaceType) type.getType()
				if (realType.isBoxedType()) {
					return true
				} else {
					String name = realType.name
					if (imports.entrySet().contains(name)) {
						String qualifiedName = imports.get(name)
						return qualifiedName.startsWith("java") || qualifiedName.startsWith("javax")
					}
					return false
				}
			}
		}
		return true
	}

	static boolean isJavaObject(String s) {
		return "Object".equals(s) || "String".equals(s)
	}
}
