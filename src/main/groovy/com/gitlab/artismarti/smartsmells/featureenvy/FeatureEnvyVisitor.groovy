package com.gitlab.artismarti.smartsmells.featureenvy

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.gitlab.artismarti.smartsmells.common.CustomVariableDeclaration
import com.gitlab.artismarti.smartsmells.common.PackageImportHolder
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.common.helper.*
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.cycle.InnerClassesHandler

import java.nio.file.Path
/**
 * @author artur
 */
class FeatureEnvyVisitor extends Visitor<FeatureEnvy> {

	private FeatureEnvyFactor featureEnvyFactor

	private Set<CustomVariableDeclaration> fields
	private List<ImportDeclaration> imports

	private String currentClassName
	private PackageImportHolder packageImportHolder
	private InnerClassesHandler innerClassesHandler

	FeatureEnvyVisitor(Path path, FeatureEnvyFactor factor) {
		super(path)
		this.featureEnvyFactor = factor
	}

	@Override
	void visit(CompilationUnit n, Object arg) {
		imports = ASTHelper.getNodesByType(n, ImportDeclaration.class)
		packageImportHolder = new PackageImportHolder(n.package, n.imports)
		innerClassesHandler = new InnerClassesHandler(n)
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
		MethodHelper.filterAnonymousMethods(methods)
				.stream()
				.filter { MethodHelper.sizeBiggerThan(2, it) }.each {


			def parent = NodeHelper.findDeclaringClass(it)
			parent.ifPresent {
				def type = new ClassOrInterfaceType(((ClassOrInterfaceDeclaration) parent.get()).name)
				currentClassName = innerClassesHandler.getUnqualifiedNameForInnerClass(type)
			}

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

			if (factor > featureEnvyFactor.threshold) {
				def roundedFactor = (factor * 100).toInteger().toDouble() / 100

				def featureEnvy = new FeatureEnvy(
						method.name, method.declarationAsString, currentClassName,
						it.name, it.type.toString(), it.nature.toString(),
						roundedFactor, featureEnvyFactor.threshold,
						SourcePath.of(path), it.sourceRange)

				smells.add(featureEnvy)
			}
		}
	}

	private double calc(int entityCalls, int allCalls) {
		if (allCalls == 0 || allCalls == 1) {
			return 0.0;
		}

		def weight = featureEnvyFactor.weight
		return weight * (entityCalls / allCalls) + (1 - weight) * (1 - Math.pow(featureEnvyFactor.base, entityCalls));
	}

}
