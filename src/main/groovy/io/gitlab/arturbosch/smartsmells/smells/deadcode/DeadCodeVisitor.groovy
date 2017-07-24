package io.gitlab.arturbosch.smartsmells.smells.deadcode

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.LocaleVariableHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author artur
 */
@CompileStatic
class DeadCodeVisitor extends Visitor<DeadCode> {

	public static final String SERIAL_VERSION_UID = "serialVersionUID"

	private boolean onlyPrivate

	private Map<String, MutableInt> methodsToReferenceCount = new HashMap<>()
	private Map<String, MethodDeclaration> methodToMethodDeclaration = new HashMap<>()

	private Map<String, MutableInt> fieldsToReferenceCount = new HashMap<>()
	private Map<String, FieldDeclaration> fieldsToFieldDeclaration = new HashMap<>()

	private Map<String, MutableInt> parameterToReferenceCount = new HashMap<>()
	private Map<String, Parameter> parameterToParameterDeclaration = new HashMap<>()

	private Map<String, MutableInt> localeVariableToReferenceCount = new HashMap<>()
	private Map<String, VariableDeclarationExpr> localeVariableToVariableDeclaration = new HashMap<>()

	DeadCodeVisitor(boolean onlyPrivate) {
		this.onlyPrivate = onlyPrivate
	}

	@Override
	void visit(CompilationUnit n, Resolver resolver) {
		if (isInterface(n))
			return

		def methodDeclarations = DeadCodeHelper.filterMethodsForAnnotations(NodeHelper.findPrivateMethods(n))
		methodDeclarations.each {
			methodsToReferenceCount.put(it.nameAsString, new MutableInt())
			methodToMethodDeclaration.put(it.nameAsString, it)
		}

		def variableDeclarations = NodeHelper.findPrivateFields(n)
		for (FieldDeclaration field : variableDeclarations) {
			for (VariableDeclarator variable : field.variables) {
				if (variable.nameAsString != SERIAL_VERSION_UID) {
					fieldsToReferenceCount.put(variable.nameAsString, new MutableInt())
					fieldsToFieldDeclaration.put(variable.nameAsString, field)
				}
			}
		}

		def allMethods = NodeHelper.findMethods(n)
		def parameters = DeadCodeHelper.parametersFromAllMethodDeclarationsAsStringSet(allMethods)
		parameters.each {
			parameterToReferenceCount.put(it.nameAsString, new MutableInt())
			parameterToParameterDeclaration.put(it.nameAsString, it)
		}

		def localeVariables = LocaleVariableHelper.find(allMethods)
		for (VariableDeclarationExpr localExpr : localeVariables) {
			for (VariableDeclarator variable : localExpr.variables) {
				localeVariableToReferenceCount.put(variable.nameAsString, new MutableInt())
				localeVariableToVariableDeclaration.put(variable.nameAsString, localExpr)
			}
		}

		new ReferenceVisitor(
				methodsToReferenceCount,
				fieldsToReferenceCount,
				localeVariableToReferenceCount,
				parameterToReferenceCount
		).visit(n, resolver)

		addSmells()

	}

	private static boolean isInterface(CompilationUnit n) {
		def types = n.types
		if (types.size() == 1) {
			def classOrInterface = types[0]
			if (classOrInterface instanceof ClassOrInterfaceDeclaration)
				return classOrInterface.isInterface()
		}
		return false
	}

	private void addSmells() {
		methodsToReferenceCount.entrySet().stream()
				.filter { it.value.get() == 0 }
				.map { methodToMethodDeclaration.get(it.key) }
				.forEach {
			smells.add(new DeadCode(it.nameAsString, it.declarationAsString, SourcePath.of(info),
					SourceRange.fromNode(it), ElementTarget.METHOD))
		}

		fieldsToReferenceCount.entrySet().stream()
				.filter { it.value.get() == 0 }
				.forEach {
			def field = fieldsToFieldDeclaration.get(it.key)
			smells.add(new DeadCode(it.key, Printer.toString(field), SourcePath.of(info),
					SourceRange.fromNode(field), ElementTarget.FIELD))
		}

		parameterToReferenceCount.entrySet().stream()
				.filter { it.value.get() == 0 }
				.map { parameterToParameterDeclaration.get(it.key) }
				.forEach {
			smells.add(new DeadCode(it.nameAsString, Printer.toString(it), SourcePath.of(info),
					SourceRange.fromNode(it), ElementTarget.PARAMETER))
		}

		localeVariableToReferenceCount.entrySet().stream()
				.filter { it.value.get() == 0 }
				.forEach {
			def var = localeVariableToVariableDeclaration.get(it.key)
			smells.add(new DeadCode(it.key, Printer.toString(var), SourcePath.of(info),
					SourceRange.fromNode(var), ElementTarget.LOCAL))
		}
	}

}
