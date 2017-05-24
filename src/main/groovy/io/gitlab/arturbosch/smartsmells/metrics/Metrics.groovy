package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.MethodHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.TypeHelper
import io.gitlab.arturbosch.jpal.ast.VariableHelper
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.helper.NameHelper
import io.gitlab.arturbosch.smartsmells.common.visitor.CyclomaticComplexityVisitor
import io.gitlab.arturbosch.smartsmells.smells.godclass.FieldAccessVisitor
import io.gitlab.arturbosch.smartsmells.smells.godclass.TiedClassCohesion
import io.gitlab.arturbosch.smartsmells.util.JavaLoc

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
					.collect()

			cm = resolver.storage.getAllCompilationInfo()
					.stream()
					.filter { it.isWithinScope(type) }
					.map { it.unit.getNodesByType(MethodCallExpr.class) }
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

	static int mcCabe(BodyDeclaration n) {
		def complexityVisitor = new CyclomaticComplexityVisitor()
		n.accept(complexityVisitor, null)
		return complexityVisitor.mcCabeComplexity + 1
	}

	static int wmc(ClassOrInterfaceDeclaration n) {
		MethodHelper.filterAnonymousMethods(NodeHelper.findMethods(n))
				.stream()
				.filter { ClassHelper.inClassScope(it, n.nameAsString) }
				.mapToInt { mcCabe(it) }
				.sum()
	}

	static int atfd(ClassOrInterfaceDeclaration n) {
		int atfd = 0

		def fields = getClassFieldNames(n)
		def methods = getClassMethodNames(n)


		Set<String> usedScopes = new HashSet<>()
		n.getNodesByType(MethodCallExpr.class)
				.stream()
				.filter { isNotMemberOfThisClass(it.nameAsString, methods) }
				.filter { isNotAGetterOrSetter(it.nameAsString) }
				.each {

			if (it.scope.isPresent() && !usedScopes.contains(it.scope.get().toString(Printer.NO_COMMENTS))) {
				usedScopes.add(it.scope.get().toString(Printer.NO_COMMENTS))
				atfd++
			}
		}

		usedScopes = new HashSet<>()
		n.getNodesByType(FieldAccessExpr.class)
				.stream()
				.filter { isNotMemberOfThisClass(it.name.identifier, fields) }
				.each {

			if (!usedScopes.contains(it.scope.toString(Printer.NO_COMMENTS))) {
				usedScopes.add(it.scope.toString(Printer.NO_COMMENTS))
				atfd++
			}
		}
		return atfd
	}

	private static List getClassMethodNames(ClassOrInterfaceDeclaration n) {
		NodeHelper.findMethods(n).stream()
				.filter { ClassHelper.inClassScope(it, n.nameAsString) }
				.map { it.name }
				.collect(Collectors.toList())
	}

	private static List<String> getClassFieldNames(ClassOrInterfaceDeclaration n) {
		NameHelper.toFieldNames(
				NodeHelper.findFields(n).stream()
						.filter { ClassHelper.inClassScope(it, n.nameAsString) }
						.collect(Collectors.toList()))
	}

	private static boolean isNotMemberOfThisClass(String name, List<String> members) {
		!members.contains(name)
	}

	private static boolean isNotAGetterOrSetter(String name) {
		!name.startsWith("get") && !name.startsWith("set") && !name.startsWith("is")
	}

	static double tcc(ClassOrInterfaceDeclaration n) {
		Map<String, Set<String>> methodFieldAccesses = new HashMap<>()

		List<FieldDeclaration> declarations = NodeHelper.findFields(n)
				.stream()
				.filter { ClassHelper.inClassScope(it, n.nameAsString) }
				.collect()
		List<String> fields = VariableHelper.toJpalFromFields(declarations)
				.collect { it.name }

		NodeHelper.findMethods(n)
				.stream()
				.filter { ClassHelper.inClassScope(it, n.nameAsString) }
				.filter { it.modifiers.contains(Modifier.PUBLIC) }
				.each { collectFieldAccesses(it, methodFieldAccesses, fields) }

		return TiedClassCohesion.calc(methodFieldAccesses)
	}

	private static void collectFieldAccesses(MethodDeclaration n,
											 Map<String, Set<String>> methodFieldAccesses,
											 List<String> fields) {
		def visitor = new FieldAccessVisitor(fields)
		n.accept(visitor, null)

		def accessedFieldNames = visitor.fieldNames
		def methodName = n.nameAsString

		methodFieldAccesses.put(methodName, accessedFieldNames)
	}

	static int sloc(ClassOrInterfaceDeclaration n) {
		return locInternal(n, false)
	}

	static int loc(ClassOrInterfaceDeclaration n) {
		return locInternal(n, true)
	}

	private static int locInternal(ClassOrInterfaceDeclaration n, boolean comments) {
		def lines = n.toString().split(System.getProperty("line.separator")).toList()
		return JavaLoc.analyze(lines, comments, false)
	}

}
