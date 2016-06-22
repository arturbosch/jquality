package com.gitlab.artismarti.smartsmells.common.helper

import com.github.javaparser.ast.body.FieldDeclaration
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

	static String extractName(FieldDeclaration field) {
		return field.variables.id.name
	}

}
