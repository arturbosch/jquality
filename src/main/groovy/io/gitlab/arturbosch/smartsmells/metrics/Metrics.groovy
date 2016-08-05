package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.body.*
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.gitlab.artismarti.smartsmells.common.helper.*
import io.gitlab.arturbosch.smartsmells.common.CompilationStorage
import io.gitlab.arturbosch.smartsmells.common.helper.*
import io.gitlab.arturbosch.smartsmells.common.visitor.CyclomaticComplexityVisitor
import io.gitlab.arturbosch.smartsmells.smells.godclass.FieldAccessVisitor
import io.gitlab.arturbosch.smartsmells.smells.godclass.TiedClassCohesion
import io.gitlab.arturbosch.smartsmells.util.JavaLoc
import io.gitlab.arturbosch.smartsmells.util.StreamCloser

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

/**
 * @author artur
 */
final class Metrics {

	static int cm(ClassOrInterfaceDeclaration n) {
		int cm = -1
		TypeHelper.getQualifiedType(n)
				.ifPresent { type ->

			def methods = NodeHelper.findMethods(n)
					.stream()
					.filter { ClassHelper.inCurrentClass(it, n.name) }
					.map { it.name }
					.collect()

			cm = CompilationStorage.getAllCompilationInfo()
					.stream()
					.filter { it.isWithinScope(type) }
					.map { ASTHelper.getNodesByType(it.unit, MethodCallExpr.class) }
					.flatMap { it.stream() }
					.filter { methods.contains(it.name) }
					.mapToInt { 1 }
					.sum()

		}
		return cm
	}

	static int cc(ClassOrInterfaceDeclaration n) {
		int cc = -1
		TypeHelper.getQualifiedType(n)
				.ifPresent { type ->
			cc = CompilationStorage.getAllCompilationInfo()
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

		def fields = getClassFieldNames(n)
		def methods = getClassMethodNames(n)


		Set<String> usedScopes = new HashSet<>()
		ASTHelper.getNodesByType(n, MethodCallExpr.class)
				.stream()
				.filter { isNotMemberOfThisClass(it.name, methods) }
				.filter { isNotAGetterOrSetter(it.name) }
				.each {

			if (it.scope && !usedScopes.contains(it.scope.toStringWithoutComments())) {
				usedScopes.add(it.scope.toStringWithoutComments())
				atfd++
			}
		}

		usedScopes = new HashSet<>()
		ASTHelper.getNodesByType(n, FieldAccessExpr.class)
				.stream()
				.filter { isNotMemberOfThisClass(it.field, fields) }
				.each {

			if (it.scope && !usedScopes.contains(it.scope.toStringWithoutComments())) {
				usedScopes.add(it.scope.toStringWithoutComments())
				atfd++
			}
		}
		return atfd
	}

	private static List getClassMethodNames(ClassOrInterfaceDeclaration n) {
		NodeHelper.findMethods(n).stream()
				.filter { ClassHelper.inCurrentClass(it, n.name) }
				.map { it.name }
				.collect(Collectors.toList())
	}

	private static List<String> getClassFieldNames(ClassOrInterfaceDeclaration n) {
		NameHelper.toFieldNames(
				NodeHelper.findFields(n).stream()
						.filter { ClassHelper.inCurrentClass(it, n.name) }
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
				.filter { ClassHelper.inCurrentClass(it, n.name) }
				.collect()
		List<String> fields = VariableHelper.fromFieldToCustomVariableDeclarations(declarations)
				.collect { it.name }

		NodeHelper.findMethods(n)
				.stream()
				.filter { ClassHelper.inCurrentClass(it, n.name) }
				.filter { ModifierSet.isPublic(it.modifiers) }
				.each { collectFieldAccesses(it, methodFieldAccesses, fields) }

		return TiedClassCohesion.calc(methodFieldAccesses)
	}

	private static void collectFieldAccesses(MethodDeclaration n,
	                                         Map<String, Set<String>> methodFieldAccesses,
	                                         List<String> fields) {
		def visitor = new FieldAccessVisitor(fields)
		n.accept(visitor, null)

		def accessedFieldNames = visitor.fieldNames
		def methodName = n.name

		methodFieldAccesses.put(methodName, accessedFieldNames)
	}

	static int sloc(ClassOrInterfaceDeclaration n, Path path) {
		return locInternal(n, path, false)
	}

	static int loc(ClassOrInterfaceDeclaration n, Path path) {
		return locInternal(n, path, true)
	}

	private static int locInternal(ClassOrInterfaceDeclaration n, Path path, boolean comments) {
		if (!path.toString().endsWith(".java")) return -1

		def javaDoc = Optional.ofNullable(n.comment)
				.map { it.endLine - it.beginLine + 1 }
				.orElse(0)

		def sourceRange = BadSmellHelper.createSourceRangeFromNode(n)
		def i = sourceRange.startLine() - 1 - javaDoc

		def start = i > 0 ? i : 0
		def end = sourceRange.endLine() - sourceRange.startLine() + 1 + javaDoc

		def stream = Files.lines(path)
		def collect = stream
				.skip(start)
				.limit(end)
				.filter { !it.empty }
				.collect(Collectors.toList())
		StreamCloser.quietly(stream)

		return JavaLoc.analyze(collect, comments, false)
	}

}
