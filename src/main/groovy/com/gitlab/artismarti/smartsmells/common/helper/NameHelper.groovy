package com.gitlab.artismarti.smartsmells.common.helper

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration

/**
 * @author artur
 */
class NameHelper {

	private NameHelper() {}

	static List<String> toFieldNames(List<FieldDeclaration> fields) {
		fields.stream()
				.map { it.variables }
				.map { it.id }
				.map { it.name }
				.flatMap { it.stream() }
				.collect { (String) it }
	}

	static List<String> toMethodNames(List<MethodDeclaration> methods) {
		methods.collect({ it.name })
	}
}
