package io.gitlab.arturbosch.smartsmells.smells.deadcode

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.CastExpr
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.MethodReferenceExpr
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.stmt.ForStmt
import com.github.javaparser.ast.stmt.ForeachStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.ReturnStmt
import com.github.javaparser.ast.stmt.SwitchStmt
import com.github.javaparser.ast.stmt.WhileStmt
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
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

	DeadCodeVisitor(boolean onlyPrivate) {
		this.onlyPrivate = onlyPrivate
	}

	@Override
	void visit(CompilationUnit n, Resolver resolver) {
		if (isInterface(n))
			return

		def methodDeclarations = DeadCodeHelper.filterMethodsForAnnotations(NodeHelper.findPrivateMethods(n))
		methodsToReferenceCount = methodDeclarations.collectEntries { [it.nameAsString, 0] }
		methodToMethodDeclaration = methodDeclarations.collectEntries { [it.nameAsString, it] }

		def variableDeclarations = NodeHelper.findPrivateFields(n)
		createFieldMaps(variableDeclarations)

		def allMethods = NodeHelper.findMethods(n)
		def parameters = DeadCodeHelper.parametersFromAllMethodDeclarationsAsStringSet(allMethods)
		parameterToReferenceCount = parameters.collectEntries { [it.nameAsString, 0] }
		parameterToParameterDeclaration = parameters.collectEntries { [it.nameAsString, it] }

		def localeVariables = LocaleVariableHelper.find(allMethods)
		createLocaleVariableMaps(localeVariables)

		new ReferenceVisitor().visit(n, resolver)

		addSmells()

	}

	private static boolean isInterface(CompilationUnit n) {
		def types = n.types
		if (types.size() == 1) {
			def classOrInterface = types[0]
			if (classOrInterface instanceof ClassOrInterfaceDeclaration)
				return classOrInterface.interface
		}
		return false
	}

	private void addSmells() {
		methodsToReferenceCount.entrySet().stream()
				.filter { it.value == 0 }
				.map { methodToMethodDeclaration.get(it.key) }
				.forEach {
			smells.add(new DeadCode(it.nameAsString, it.declarationAsString, SourcePath.of(path),
					SourceRange.fromNode(it), ElementTarget.METHOD))
		}

		fieldsToReferenceCount.entrySet().stream()
				.filter { it.value == 0 }
				.forEach {
			def field = fieldsToFieldDeclaration.get(it.key)
			smells.add(new DeadCode(it.key, field.toString(Printer.NO_COMMENTS), SourcePath.of(path),
					SourceRange.fromNode(field), ElementTarget.FIELD))
		}

		parameterToReferenceCount.entrySet().stream()
				.filter { it.value == 0 }
				.map { parameterToParameterDeclaration.get(it.key) }
				.forEach {
			smells.add(new DeadCode(it.nameAsString, it.toString(Printer.NO_COMMENTS), SourcePath.of(path),
					SourceRange.fromNode(it), ElementTarget.PARAMETER))
		}

		localeVariableToReferenceCount.entrySet().stream()
				.filter { it.value == 0 }
				.forEach {
			def var = localeVariableToVariableDeclaration.get(it.key)
			smells.add(new DeadCode(it.key, var.toString(Printer.NO_COMMENTS), SourcePath.of(path),
					SourceRange.fromNode(var), ElementTarget.LOCAL))
		}
	}

	private void createFieldMaps(List<FieldDeclaration> variableDeclarations) {
		variableDeclarations.each { declaration ->
			declaration.variables.stream()
					.filter({ it.nameAsString != "serialVersionUID" }).each {
				fieldsToReferenceCount.put(it.nameAsString, 0)
				fieldsToFieldDeclaration.put(it.nameAsString, declaration)
			}
		}
	}

	private void createLocaleVariableMaps(List<VariableDeclarationExpr> variableDeclarations) {
		variableDeclarations.each { declaration ->
			declaration.variables.each {
				localeVariableToReferenceCount.put(it.nameAsString, 0)
				localeVariableToVariableDeclaration.put(it.nameAsString, declaration)
			}
		}
	}


	private void checkArguments(Expression it) {
		Optional.ofNullable(it)
				.map { it.toString(Printer.NO_COMMENTS) }
				.ifPresent {
			checkOccurrence(fieldsToReferenceCount, it)
			checkOccurrence(parameterToReferenceCount, it)
			checkOccurrence(localeVariableToReferenceCount, it)
		}
	}

	private static checkOccurrence(Map<String, Integer> map, String expr) {
		map.entrySet().stream()
				.filter { expr.contains(it.key) }
				.forEach { it.value++ }
	}

	private class ReferenceVisitor extends VoidVisitorAdapter {

		@Override
		void visit(MethodReferenceExpr n, Object arg) {
			methodsToReferenceCount.computeIfPresent(n.identifier, { key, value -> value + 1 })
			super.visit(n, arg)
		}

		@Override
		void visit(AssignExpr n, Object arg) {
			checkArguments(n.value)
			super.visit(n, arg)
		}

		@Override
		void visit(CastExpr n, Object arg) {
			checkArguments(n.expression)
			super.visit(n, arg)
		}

		@Override
		void visit(BinaryExpr n, Object arg) {
			checkArguments(n.left)
			checkArguments(n.right)
			super.visit(n, arg)
		}

		@Override
		void visit(ReturnStmt n, Object arg) {
			n.expression.ifPresent { checkArguments(it) }
			super.visit(n, arg)
		}

		@Override
		void visit(MethodCallExpr n, Object arg) {
			methodsToReferenceCount.computeIfPresent(n.nameAsString, { key, value -> value + 1 })

			n.scope.ifPresent {
				checkArguments(it)
			}

			n.arguments.each {
				checkArguments(it)
			}

			super.visit(n, arg)
		}

		@Override
		void visit(FieldAccessExpr n, Object arg) {
			fieldsToReferenceCount.computeIfPresent(n.nameAsString, { key, value -> value + 1 })
			super.visit(n, arg)
		}

		@Override
		void visit(ObjectCreationExpr n, Object arg) {
			n.arguments.each {
				checkArguments(it)
			}
			super.visit(n, arg)
		}

		@Override
		void visit(ForeachStmt n, Object arg) {
			checkArguments(n.iterable)
			super.visit(n, arg)
		}

		@Override
		void visit(ForStmt n, Object arg) {
			n.compare.ifPresent { checkArguments(it) }
			super.visit(n, arg)
		}

		@Override
		void visit(IfStmt n, Object arg) {
			checkArguments(n.condition)
			super.visit(n, arg)
		}

		@Override
		void visit(WhileStmt n, Object arg) {
			checkArguments(n.condition)
			super.visit(n, arg)
		}

		@Override
		void visit(SwitchStmt n, Object arg) {
			checkArguments(n.selector)
			super.visit(n, arg)
		}
	}

}
