package io.gitlab.arturbosch.smartsmells.smells.deadcode

import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter

import java.util.stream.Collectors

/**
 * @author artur
 */
final class DeadCodeHelper {

	private DeadCodeHelper() {}

	private static final Set<String> excludedAnnotations = new HashSet<>()

	static {
		excludedAnnotations.add("PostConstruct")
		excludedAnnotations.add("Override")
	}

	static Set<Parameter> parametersFromMethods(List<MethodDeclaration> methodDeclarations) {
		methodDeclarations.stream()
				.filter { !it.modifiers.contains(Modifier.ABSTRACT) }
				.flatMap { it.parameters.stream() }
				.collect(Collectors.toSet())
	}

	static List<MethodDeclaration> filterMethodsForExcludedAnnotations(List<MethodDeclaration> methodDeclarations) {
		methodDeclarations.stream().filter {
			it.getAnnotations().find { excludedAnnotations.contains(it.nameAsString) } == null
		}.collect()
	}
}
