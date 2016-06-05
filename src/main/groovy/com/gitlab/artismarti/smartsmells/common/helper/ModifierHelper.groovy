package com.gitlab.artismarti.smartsmells.common.helper

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.ModifierSet

/**
 * @author artur
 */
class ModifierHelper {

	private ModifierHelper() {}

	static List<MethodDeclaration> findPublicMethods(List<MethodDeclaration> methods) {
		methods.stream()
				.filter { ModifierSet.isPublic(it.modifiers) }
				.collect()
	}
}
