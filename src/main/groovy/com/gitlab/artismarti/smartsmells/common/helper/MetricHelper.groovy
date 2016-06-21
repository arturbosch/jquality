package com.gitlab.artismarti.smartsmells.common.helper

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.ModifierSet
import com.gitlab.artismarti.smartsmells.common.visitor.CyclomaticComplexityVisitor
import com.gitlab.artismarti.smartsmells.smells.godclass.FieldAccessVisitor
import com.gitlab.artismarti.smartsmells.smells.godclass.TiedClassCohesion

/**
 * @author artur
 */
final class MetricHelper {

	static int mcCabe(BodyDeclaration n) {
		def complexityVisitor = new CyclomaticComplexityVisitor()
		n.accept(complexityVisitor, null)
		return complexityVisitor.mcCabeComplexity + 1
	}

	static int wmc(ClassOrInterfaceDeclaration n) {
		MethodHelper.filterAnonymousMethods(NodeHelper.findMethods(n))
				.stream()
				.mapToInt { mcCabe(it) }
				.sum()
	}

	static int noa(ClassOrInterfaceDeclaration n) {
		NodeHelper.findFields(n).size()
	}

	static int nom(ClassOrInterfaceDeclaration n) {
		MethodHelper.filterAnonymousMethods(NodeHelper.findMethods(n)).size()
	}

	static double tcc(ClassOrInterfaceDeclaration n) {
		Map<String, Set<String>> methodFieldAccesses = new HashMap<>()

		List<String> publicMethods = NodeHelper.findMethods(n)
				.stream()
				.filter { ModifierSet.isPublic(it.modifiers) }
				.map { it.name }
				.collect()

		String className = n.name
		ASTHelper.getNodesByType(n, MethodDeclaration)
				.stream()
				.filter { inClassScope(it, className) }
				.filter { publicMethods.contains(it.name) }
				.each { collectFieldAccesses(it, methodFieldAccesses) }

		return TiedClassCohesion.calc(methodFieldAccesses)
	}

	private static boolean inClassScope(Node node, String currentClassName) {
		NodeHelper.findDeclaringClass(node)
				.filter { it.name == currentClassName }
				.isPresent()
	}

	private static void collectFieldAccesses(MethodDeclaration n,
	                                         Map<String, Set<String>> methodFieldAccesses) {
		def visitor = new FieldAccessVisitor()
		n.accept(visitor, null)

		def accessedFieldNames = visitor.fieldNames
		def methodName = n.name

		methodFieldAccesses.put(methodName, accessedFieldNames)
	}

}
