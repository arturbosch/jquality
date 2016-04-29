package com.gitlab.artismarti.smartsmells.featureenvy

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.gitlab.artismarti.smartsmells.common.*
import com.gitlab.artismarti.smartsmells.deadcode.LocaleVariableHelper

import java.nio.file.Path

/**
 * @author artur
 */
class FeatureEnvyVisitor extends Visitor<FeatureEnvy> {

	private double threshold

	private Set<CustomVariableDeclaration> fields

	FeatureEnvyVisitor(Path path, double threshold) {
		super(path)
		this.threshold = threshold
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {
		if (TypeHelper.isEmptyBody(n)) return
		if (TypeHelper.hasNoMethods(n)) return

		fields = VariableHelper.fromFieldToCustomVariableDeclarations(NodeHelper.findFields(n))

		analyzeMethods(NodeHelper.findMethods(n))
	}

	private analyzeMethods(List<MethodDeclaration> methods) {
		methods.each {
			def allCalls = MethodHelper.getAllMethodInvocations(it)

			def parameters = VariableHelper.toCustomVariableDeclarations(MethodHelper.extractParameters(it))
			def variables = VariableHelper.toCustomVariableDeclarations(LocaleVariableHelper.find(it))

			analyzeVariables(it, allCalls, variables)
			analyzeVariables(it, allCalls, parameters)
			analyzeVariables(it, allCalls, fields)

		}
	}

	private static analyzeVariables(MethodDeclaration method, int allCalls, Set<CustomVariableDeclaration> variables) {
		variables.forEach {
			int count = MethodHelper.getAllMethodInvocationsForEntityWithName(it.name, method)
			println "$it.name has count $count"
		}
	}
}
