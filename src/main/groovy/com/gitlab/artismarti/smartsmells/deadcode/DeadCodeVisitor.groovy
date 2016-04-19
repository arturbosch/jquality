package com.gitlab.artismarti.smartsmells.deadcode

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.ReturnStmt
import com.gitlab.artismarti.smartsmells.common.BadSmellHelper
import com.gitlab.artismarti.smartsmells.common.NodeHelper
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.domain.SourcePath

import java.nio.file.Path

/**
 * @author artur
 */
class DeadCodeVisitor extends Visitor<DeadCode> {

	private boolean onlyPrivate

	private Map<String, Integer> methodsToReferenceCount = new HashMap<>()
	private Map<String, MethodDeclaration> methodToMethodDeclaration = new HashMap<>()

	private Map<String, Integer> fieldsToReferenceCount = new HashMap<>()
	private Map<String, FieldDeclaration> fieldsToFieldDeclaration = new HashMap<>()

	private Map<String, Integer> parameterToReferenceCount = new HashMap<>()
	private Map<String, Parameter> parameterToParameterDeclaration = new HashMap<>()

	private Map<String, Integer> localeVariableToReferenceCount = new HashMap<>()
	private Map<String, VariableDeclarationExpr> localeVariableToVariableDeclaration = new HashMap<>()

	DeadCodeVisitor(Path path, boolean onlyPrivate) {
		super(path)
		this.onlyPrivate = onlyPrivate
	}

	@Override
	void visit(CompilationUnit n, Object arg) {

		def methodDeclarations = NodeHelper.findPrivateMethods(n)
		methodsToReferenceCount = methodDeclarations.collectEntries { [it.name, 0] }
		methodToMethodDeclaration = methodDeclarations.collectEntries { [it.name, it] }

		def variableDeclarations = NodeHelper.findPrivateFields(n)
		createFieldMaps(variableDeclarations)

		def parameters = DeadCodeHelper.parametersFromAllMethodDeclarationsAsStringSet(methodDeclarations)
		parameterToReferenceCount = parameters.collectEntries { [it.id.name, 0] }
		parameterToParameterDeclaration = parameters.collectEntries { [it.id.name, it] }

		def localeVariables = LocaleVariableHelper.find(methodDeclarations)
		createLocaleVariableMaps(localeVariables)

		super.visit(n, arg)

		println methodsToReferenceCount
		println fieldsToReferenceCount
		println parameterToReferenceCount
		println localeVariableToReferenceCount
		addSmells()

	}

	private void addSmells() {
		methodsToReferenceCount.entrySet().stream()
				.filter { it.value == 0 }
				.map { methodToMethodDeclaration.get(it.key) }
				.forEach {
			smells.add(new DeadCode(it.name, it.declarationAsString, SourcePath.of(path),
					BadSmellHelper.createSourceRangeFromNode(it)))
		}

		fieldsToReferenceCount.entrySet().stream()
				.filter { it.value == 0 }
				.forEach {
			def field = fieldsToFieldDeclaration.get(it.key)
			smells.add(new DeadCode(it.key, field.toStringWithoutComments(), SourcePath.of(path),
					BadSmellHelper.createSourceRangeFromNode(field)))
		}

		parameterToReferenceCount.entrySet().stream()
				.filter { it.value == 0 }
				.map { parameterToParameterDeclaration.get(it.key) }
				.forEach {
			smells.add(new DeadCode(it.id.name, it.toStringWithoutComments(), SourcePath.of(path),
					BadSmellHelper.createSourceRangeFromNode(it)))
		}

		localeVariableToReferenceCount.entrySet().stream()
				.filter { it.value == 0 }
				.forEach {
			def var = localeVariableToVariableDeclaration.get(it.key)
			smells.add(new DeadCode(it.key, var.toStringWithoutComments(), SourcePath.of(path),
					BadSmellHelper.createSourceRangeFromNode(var)))
		}
	}

	private void createFieldMaps(List<FieldDeclaration> variableDeclarations) {
		variableDeclarations.each { declaration ->
			declaration.variables.each {
				fieldsToReferenceCount.put(it.id.name, 0)
				fieldsToFieldDeclaration.put(it.id.name, declaration)
			}
		}
	}

	private void createLocaleVariableMaps(List<VariableDeclarationExpr> variableDeclarations) {
		variableDeclarations.each { declaration ->
			declaration.vars.each {
				localeVariableToReferenceCount.put(it.id.name, 0)
				localeVariableToVariableDeclaration.put(it.id.name, declaration)
			}
		}
	}

	@Override
	void visit(MethodReferenceExpr n, Object arg) {
		methodsToReferenceCount.computeIfPresent(n.identifier, { key, value -> value + 1 })
		super.visit(n, arg)
	}

	@Override
	void visit(AssignExpr n, Object arg) {
		def maybeField = n.target.toStringWithoutComments()
		if (fieldsToReferenceCount.keySet().contains(maybeField)) {
			def expr = n.value.toStringWithoutComments()
			checkOccurrence(localeVariableToReferenceCount, expr)
			checkOccurrence(parameterToReferenceCount, expr)
		}
		super.visit(n, arg)
	}

	@Override
	void visit(ReturnStmt n, Object arg) {
		def expr = n.expr.toStringWithoutComments()
		checkOccurrence(fieldsToReferenceCount, expr)
		checkOccurrence(parameterToReferenceCount, expr)
		checkOccurrence(localeVariableToReferenceCount, expr)
		super.visit(n, arg)
	}

	private static checkOccurrence(Map<String, Integer> map, String expr) {
		map.entrySet().stream()
				.filter { expr.contains(it.key) }
				.forEach { it.value++ }
	}

	@Override
	void visit(MethodCallExpr n, Object arg) {
		methodsToReferenceCount.computeIfPresent(n.name, { key, value -> value + 1 })

		checkMethodCaller(n)

		n.args.each {
			checkOccurrence(fieldsToReferenceCount, it.toStringWithoutComments())
		}

		super.visit(n, arg)
	}

	private checkMethodCaller(MethodCallExpr n) {
		Optional.ofNullable(n.scope)
				.map({ it.toStringWithoutComments() })
				.ifPresent({
			fieldsToReferenceCount.computeIfPresent(it, { key, value -> value + 1 })
		})
	}

	@Override
	void visit(FieldAccessExpr n, Object arg) {
		println n.field
		fieldsToReferenceCount.computeIfPresent(n.field, { key, value -> value + 1 })
		super.visit(n, arg)
	}
}
