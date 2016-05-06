package com.gitlab.artismarti.smartsmells.common.helper

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.ModifierSet

import java.util.stream.Collectors

/**
 * @author artur
 */
class NodeHelper {

	static List<MethodDeclaration> findPrivateMethods(Node n) {
		findMethods(n).stream()
				.filter({ ModifierSet.isPrivate(it.modifiers) })
				.collect(Collectors.toList())
	}

	static List<String> findPublicMethods(Node n) {
		findMethods(n).stream()
				.filter({ ModifierSet.isPublic(it.modifiers) })
				.collect({ it.name })
	}

	static List<MethodDeclaration> findMethods(Node n) {
		ASTHelper.getNodesByType(n, MethodDeclaration.class)
	}

	static List<String> findMethodNames(Node n) {
		findMethods(n).collect({ it.name })
	}

	static List<String> findFieldNames(Node n) {
		findFields(n)
				.collect({ it.variables })
				.collect({ it.id })
				.collect({ it.name })
				.flatten()
				.collect({ (String) it })
	}

	static List<FieldDeclaration> findPrivateFields(Node n) {
		findFields(n)
				.stream()
				.filter({ ModifierSet.isPrivate(it.modifiers) })
				.collect()
	}

	static List<FieldDeclaration> findFields(Node n) {
		ASTHelper.getNodesByType(n, FieldDeclaration.class)
	}

	static Set<String> findNamesOfInnerClasses(Node n) {
		ASTHelper.getNodesByType(n, ClassOrInterfaceDeclaration.class).stream()
				.filter { it.parentNode instanceof ClassOrInterfaceDeclaration }
				.map { it.name }
				.collect()
	}
}
