package com.gitlab.artismarti.smartsmells.deadcode

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter

import java.util.stream.Collectors
/**
 * @author artur
 */
class DeadCodeHelper {

	static Set<Parameter> parametersFromAllMethodDeclarationsAsStringSet(List<MethodDeclaration> methodDeclarations) {
		methodDeclarations.stream()
				.flatMap({ it.parameters.stream() })
				.collect(Collectors.toSet())
	}

}
