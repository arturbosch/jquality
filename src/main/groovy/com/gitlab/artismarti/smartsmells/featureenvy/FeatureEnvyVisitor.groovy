package com.gitlab.artismarti.smartsmells.featureenvy

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.gitlab.artismarti.smartsmells.common.*
import com.gitlab.artismarti.smartsmells.deadcode.LocaleVariableHelper
import com.gitlab.artismarti.smartsmells.common.source.SourcePath

import java.nio.file.Path

/**
 * @author artur
 */
class FeatureEnvyVisitor extends Visitor<FeatureEnvy> {

	private double threshold
	private double weight = 0.5
	private double base = 0.5

	private Set<CustomVariableDeclaration> fields
	private List<ImportDeclaration> imports

	FeatureEnvyVisitor(Path path, double threshold) {
		super(path)
		this.threshold = threshold
	}

	@Override
	void visit(CompilationUnit n, Object arg) {
		imports = ASTHelper.getNodesByType(n, ImportDeclaration.class)
		super.visit(n, arg)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {
		if (TypeHelper.isEmptyBody(n)) return
		if (TypeHelper.hasNoMethods(n)) return

		fields = VariableHelper.fromFieldToCustomVariableDeclarations(NodeHelper.findFields(n))

		analyzeMethods(NodeHelper.findMethods(n))
	}

	private analyzeMethods(List<MethodDeclaration> methods) {
		def filter = new JavaClassFilter(imports)
		methods.stream().filter { MethodHelper.sizeBiggerThan(2, it) }.each {

			def allCalls = MethodHelper.getAllMethodInvocations(it)

			def parameters = VariableHelper.toCustomVariableDeclarations(MethodHelper.extractParameters(it))
			def variables = VariableHelper.toCustomVariableDeclarations(LocaleVariableHelper.find(it))

			analyzeVariables(it, allCalls, filter.forJavaClasses(variables))
			analyzeVariables(it, allCalls, filter.forJavaClasses(parameters))
			analyzeVariables(it, allCalls, filter.forJavaClasses(fields))

		}
	}

	private analyzeVariables(MethodDeclaration method, int allCalls, Set<CustomVariableDeclaration> variables) {
		variables.forEach {
			int count = MethodHelper.getAllMethodInvocationsForEntityWithName(it.name, method)
			double factor = calc(count, allCalls)

			if (factor > threshold) {
				smells.add(new FeatureEnvy(method.name, method.declarationAsString, it.name,
						it.type.toString(), factor, SourcePath.of(path), it.sourceRange))
			}
		}
	}

	private double calc(int entityCalls, int allCalls) {
		if (allCalls == 0 || allCalls == 1) {
			return 0.0;
		}

		return weight * (entityCalls / allCalls) + (1 - weight) * (1 - Math.pow(base, entityCalls));
	}

}
