package com.gitlab.artismarti.smartsmells.deadcode

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.ModifierSet
import com.github.javaparser.ast.body.Parameter

import java.util.stream.Collectors

/**
 * @author artur
 */
class DeadCodeHelper {

	static Set<Parameter> parametersFromAllMethodDeclarationsAsStringSet(List<MethodDeclaration> methodDeclarations) {
		methodDeclarations.stream()
				.filter({ !ModifierSet.isAbstract(it.modifiers) })
				.flatMap({ it.parameters.stream() })
				.collect(Collectors.toSet())
	}

	static List<MethodDeclaration> filterMethodsForAnnotations(List<MethodDeclaration> methodDeclarations) {
		methodDeclarations.stream().filter {

			!(it.getAnnotations().find { it.name.name == "PostConstruct" })

		}.collect()
	}
}
