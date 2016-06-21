package com.gitlab.artismarti.smartsmells.common.helper

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.ModifierSet
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.gitlab.artismarti.smartsmells.common.visitor.CyclomaticComplexityVisitor
import com.gitlab.artismarti.smartsmells.smells.godclass.FieldAccessVisitor
import com.gitlab.artismarti.smartsmells.smells.godclass.TiedClassCohesion

import java.util.stream.Collectors

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

	static int atfd(ClassOrInterfaceDeclaration n) {
		int atfd = 0
		def fields = NameHelper.toFieldNames(
				NodeHelper.findFields(n).stream()
						.filter { ClassHelper.inCurrentClass(it, n.name) }
						.collect(Collectors.toList()))

		def methods = NodeHelper.findMethods(n).stream()
				.filter { ClassHelper.inCurrentClass(it, n.name) }
				.map { it.name }
				.collect(Collectors.toList())

		ASTHelper.getNodesByType(n, MethodCallExpr.class).each {
			if (isNotMemberOfThisClass(it.name, methods)) {
				if (isNotAGetterOrSetter(it.name)) {
					atfd++
				}
			}
		}

		ASTHelper.getNodesByType(n, FieldAccessExpr.class).each {
			if (isNotMemberOfThisClass(it.field, fields)) {
				atfd++
			}
		}
		return atfd
	}

	private static boolean isNotMemberOfThisClass(String name, List<String> members) {
		!members.contains(name)
	}

	private static boolean isNotAGetterOrSetter(String name) {
		!name.startsWith("get") && !name.startsWith("set") && !name.startsWith("is")
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

