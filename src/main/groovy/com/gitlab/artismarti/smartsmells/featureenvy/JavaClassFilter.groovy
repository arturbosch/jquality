package com.gitlab.artismarti.smartsmells.featureenvy

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.type.PrimitiveType
import com.gitlab.artismarti.smartsmells.common.CustomVariableDeclaration
import com.gitlab.artismarti.smartsmells.common.helper.JdkHelper
import com.gitlab.artismarti.smartsmells.common.helper.TypeHelper

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

	public Set<CustomVariableDeclaration> forJavaClasses(Set<CustomVariableDeclaration> variables) {
		return variables.stream().filter { isNoPrimitiveType(it) }
				.filter { !isJavaType(it) }.collect(toSet());
	}

	private static boolean isNoPrimitiveType(CustomVariableDeclaration variable) {
		return !(variable.getType() instanceof PrimitiveType);
	}

	private boolean isJavaType(CustomVariableDeclaration variable) {

		def maybeType = TypeHelper.getClassOrInterfaceType(variable.getType())

		if (maybeType.isPresent()) {
			if (maybeType.get().isBoxedType()) {
				return true
			} else {
				String name = maybeType.get().name
				if (imports.entrySet().contains(name)) {
					String qualifiedName = imports.get(name)
					// was found inside imports, check if package does not start with java
					return qualifiedName.startsWith("java") || qualifiedName.startsWith("javax")
				} else {
					def typeOfVar = variable.type.toStringWithoutComments()
					if (JdkHelper.isPartOfJava(typeOfVar)) {
						// is inside a jdk package
						return true
					}
					// lets assume it is in the same package
					return false
				}
			}
		}
		return true
	}
}
