package io.gitlab.arturbosch.smartsmells.smells.deadcode

import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter

import java.util.stream.Collectors

/**
 * @author artur
 */
class DeadCodeHelper {

	static Set<Parameter> parametersFromAllMethodDeclarationsAsStringSet(List<MethodDeclaration> methodDeclarations) {
		methodDeclarations.stream()
				.filter({ !it.modifiers.contains(Modifier.ABSTRACT) })
				.flatMap({ it.parameters.stream() })
				.collect(Collectors.toSet())
	}

	static List<MethodDeclaration> filterMethodsForAnnotations(List<MethodDeclaration> methodDeclarations) {
		methodDeclarations.stream().filter {

			!(it.getAnnotations().find { it.nameAsString == "PostConstruct" })

		}.collect()
	}
}
