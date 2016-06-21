package com.gitlab.artismarti.smartsmells.common.helper

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

	static int noa(ClassOrInterfaceDeclaration n) {
		NodeHelper.findFields(n)
				.stream()
				.filter { ClassHelper.inCurrentClass(it, n.name) }
				.mapToInt { 1 }
				.sum()
	}

	static int nom(ClassOrInterfaceDeclaration n) {
		MethodHelper.filterAnonymousMethods(NodeHelper.findMethods(n))
				.stream()
				.filter { ClassHelper.inCurrentClass(it, n.name) }
				.mapToInt { 1 }
				.sum()
	}

	static int mcCabe(BodyDeclaration n) {
		def complexityVisitor = new CyclomaticComplexityVisitor()
		n.accept(complexityVisitor, null)
		return complexityVisitor.mcCabeComplexity + 1
	}

	static int wmc(ClassOrInterfaceDeclaration n) {
		MethodHelper.filterAnonymousMethods(NodeHelper.findMethods(n))
				.stream()
				.filter { ClassHelper.inCurrentClass(it, n.name) }
				.mapToInt { mcCabe(it) }
				.sum()
	}

	static double tcc(ClassOrInterfaceDeclaration n) {
		Map<String, Set<String>> methodFieldAccesses = new HashMap<>()

		NodeHelper.findMethods(n)
				.stream()
				.filter { ClassHelper.inCurrentClass(it, n.name) }
				.filter { ModifierSet.isPublic(it.modifiers) }
				.each { collectFieldAccesses(it, methodFieldAccesses) }

		return TiedClassCohesion.calc(methodFieldAccesses)
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

