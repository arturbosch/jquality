package io.gitlab.arturbosch.smartsmells.smells.featureenvy

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.type.PrimitiveType
import io.gitlab.arturbosch.jpal.ast.TypeHelper
import io.gitlab.arturbosch.jpal.ast.custom.JpalVariable
import io.gitlab.arturbosch.jpal.internal.JdkHelper

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

	public Set<JpalVariable> forJavaClasses(Set<JpalVariable> variables) {
		return variables.stream().filter { isNoPrimitiveType(it) }
				.filter { !isJavaType(it) }.collect(toSet());
	}

	private static boolean isNoPrimitiveType(JpalVariable variable) {
		return !(variable.getType() instanceof PrimitiveType);
	}

	private boolean isJavaType(JpalVariable variable) {

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
