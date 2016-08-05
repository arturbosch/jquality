package io.gitlab.arturbosch.smartsmells.common.helper

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.ModifierSet
import io.gitlab.arturbosch.smartsmells.util.Looper

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

	static List<MethodDeclaration> findMethods(Node n) {
		ASTHelper.getNodesByType(n, MethodDeclaration.class)
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

	static Optional<ClassOrInterfaceDeclaration> findDeclaringClass(Node node) {

		def parent = node
		Looper.loop {
			parent = parent.getParentNode()
		} until { parent instanceof ClassOrInterfaceDeclaration || parent == null }

		return parent == null ? Optional.empty() : Optional.of(parent)
	}

	static Optional<CompilationUnit> findDeclaringCompilationUnit(Node node) {

		def parent = node
		Looper.loop {
			parent = parent.getParentNode()
		} until { parent instanceof CompilationUnit || parent == null }

		return parent == null ? Optional.empty() : Optional.of(parent)
	}

	static Optional<MethodDeclaration> findDeclaringMethod(Node node) {

		def parent = node
		Looper.loop {
			parent = parent.getParentNode()
		} until { parent instanceof MethodDeclaration || parent == null }

		return parent == null ? Optional.empty() : Optional.of(parent)
	}
}
