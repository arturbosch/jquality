package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.CallableDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.MethodHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.TypeHelper
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.visitor.CyclomaticComplexityVisitor
import io.gitlab.arturbosch.smartsmells.smells.godclass.FieldAccessVisitor
import io.gitlab.arturbosch.smartsmells.smells.godclass.TiedClassCohesion

import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
final class Metrics {

	static int cm(ClassOrInterfaceDeclaration n, Resolver resolver) {
		int cm = -1
		TypeHelper.getQualifiedType(n)
				.ifPresent { type ->

			def methods = NodeHelper.findMethods(n)
					.stream()
					.filter { ClassHelper.inClassScope(it, n.nameAsString) }
					.map { it.name }
					.collect(Collectors.toSet())

			cm = resolver.storage.getAllCompilationInfo()
					.stream()
					.filter { it.isWithinScope(type) }
					.map { it.unit.getChildNodesByType(MethodCallExpr.class) }
					.flatMap { it.stream() }
					.filter { methods.contains(it.name) }
					.mapToInt { 1 }
					.sum()

		}
		return cm
	}

	static int cc(ClassOrInterfaceDeclaration n, Resolver resolver) {
		int cc = -1
		TypeHelper.getQualifiedType(n)
				.ifPresent { type ->
			cc = resolver.storage.getAllCompilationInfo()
					.stream()
					.filter { it.isWithinScope(type) }
					.mapToInt { 1 }
					.sum()
		}
		return cc
	}

	static int noa(ClassOrInterfaceDeclaration n) {
		NodeHelper.findFields(n)
				.stream()
				.filter { ClassHelper.inClassScope(it, n.nameAsString) }
				.mapToInt { 1 }
				.sum()
	}

	static int nom(ClassOrInterfaceDeclaration n) {
		MethodHelper.filterAnonymousMethods(NodeHelper.findMethods(n))
				.stream()
				.filter { ClassHelper.inClassScope(it, n.nameAsString) }
				.mapToInt { 1 }
				.sum()
	}

	static int mcCabe(CallableDeclaration n) {
		def complexityVisitor = new CyclomaticComplexityVisitor()
		n.accept(complexityVisitor, null)
		return complexityVisitor.mcCabeComplexity + 1
	}

	static int wmc(ClassOrInterfaceDeclaration n) {
		int wmc = 0
		for (MethodDeclaration method : NodeHelper.findMethods(n)) {
			if (!MethodHelper.isAnonymousMethod(method)
					&& ClassHelper.inClassScope(method, n.nameAsString)) {
				wmc += mcCabe(method)
			}
		}
		return wmc
	}

	static int atfd(ClassOrInterfaceDeclaration n) {
		int atfd = 0

		def fields = getClassFieldNames(n)
		def methods = getClassMethodNames(n)


		Set<String> usedScopes = new HashSet<>()
		n.getChildNodesByType(MethodCallExpr.class)
				.stream()
				.filter { !methods.contains(it.nameAsString) }
				.filter { isNotAGetterOrSetter(it.nameAsString) }
				.each {

			if (it.scope.isPresent()) {
				def scopeAsString = Printer.toString(it.scope.get())
				if (!usedScopes.contains(scopeAsString)) {
					usedScopes.add(scopeAsString)
					atfd++
				}
			}
		}

		usedScopes = new HashSet<>()
		n.getChildNodesByType(FieldAccessExpr.class)
				.stream()
				.filter { !fields.contains(it.nameAsString) }
				.each {

			def scopeAsString = Printer.toString(it.scope)
			if (!usedScopes.contains(scopeAsString)) {
				usedScopes.add(scopeAsString)
				atfd++
			}
		}
		return atfd
	}

	private static Set<String> getClassMethodNames(ClassOrInterfaceDeclaration n) {
		NodeHelper.findMethods(n).stream()
				.filter { ClassHelper.inClassScope(it, n.nameAsString) }
				.map { it.name }
				.collect(Collectors.toSet())
	}

	private static Set<String> getClassFieldNames(ClassOrInterfaceDeclaration n) {
		NodeHelper.findFields(n).stream()
				.filter { ClassHelper.inClassScope(it, n.nameAsString) }
				.map { it.variables }
				.flatMap { it.stream() }
				.map { it.nameAsString }
				.collect(Collectors.toSet())
	}

	private static boolean isNotAGetterOrSetter(String name) {
		!name.startsWith("get") && !name.startsWith("set") && !name.startsWith("is")
	}

	static double tcc(ClassOrInterfaceDeclaration n) {
		Map<String, Set<String>> methodFieldAccesses = new HashMap<>()

		Set<String> fields = NodeHelper.findFields(n)
				.stream()
				.filter { ClassHelper.inClassScope(it, n.nameAsString) }
				.flatMap { it.variables.stream() }
				.map { it.nameAsString }
				.collect(Collectors.toSet())

		NodeHelper.findMethods(n)
				.stream()
				.filter { ClassHelper.inClassScope(it, n.nameAsString) }
				.filter { it.modifiers.contains(Modifier.PUBLIC) }
				.each { collectFieldAccesses(it, methodFieldAccesses, fields) }

		return TiedClassCohesion.calc(methodFieldAccesses)
	}

	private static void collectFieldAccesses(MethodDeclaration n,
											 Map<String, Set<String>> methodFieldAccesses,
											 Set<String> fields) {
		def visitor = new FieldAccessVisitor(fields)
		n.accept(visitor, null)

		def accessedFieldNames = visitor.fieldNames
		def methodName = n.nameAsString

		methodFieldAccesses.put(methodName, accessedFieldNames)
	}
}
