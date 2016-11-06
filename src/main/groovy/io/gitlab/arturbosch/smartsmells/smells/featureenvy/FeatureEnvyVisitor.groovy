package io.gitlab.arturbosch.smartsmells.smells.featureenvy

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.ModifierSet
import com.github.javaparser.ast.type.ClassOrInterfaceType
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.LocaleVariableHelper
import io.gitlab.arturbosch.jpal.ast.MethodHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.VariableHelper
import io.gitlab.arturbosch.jpal.ast.custom.JpalVariable
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.nested.InnerClassesHandler
import io.gitlab.arturbosch.jpal.resolve.ResolutionData
import io.gitlab.arturbosch.smartsmells.common.Visitor

import java.nio.file.Path
import java.util.stream.Collectors

/**
 * @author artur
 */
class FeatureEnvyVisitor extends Visitor<FeatureEnvy> {

	private FeatureEnvyFactor featureEnvyFactor

	private Set<JpalVariable> fields

	private String currentClassName
	private ResolutionData resolutionData
	private InnerClassesHandler innerClassesHandler

	private boolean ignoreStatic

	FeatureEnvyVisitor(Path path, FeatureEnvyFactor factor, boolean ignoreStatic = false) {
		super(path)
		this.featureEnvyFactor = factor
		this.ignoreStatic = ignoreStatic
	}

	@Override
	void visit(CompilationUnit n, Object arg) {
		resolutionData = ResolutionData.of(n)
		innerClassesHandler = new InnerClassesHandler(n)
		super.visit(n, arg)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {

		ASTHelper.getNodesByType(n, ClassOrInterfaceDeclaration.class)
				.each { visit(it, null) }

		if (ClassHelper.isEmptyBody(n)) return
		if (ClassHelper.hasNoMethods(n)) return

		currentClassName = n.name
		def filteredFields = NodeHelper.findFields(n).stream()
				.filter { ClassHelper.inClassScope(it, currentClassName) }
				.collect(Collectors.toList())

		fields = VariableHelper.toJpalFromFields(filteredFields)

		analyzeMethods(NodeHelper.findMethods(n))
	}

	private analyzeMethods(List<MethodDeclaration> methods) {
		def filter = new JavaClassFilter(resolutionData)
		MethodHelper.filterAnonymousMethods(methods)
				.stream()
				.filter { !(ModifierSet.isStatic(it.modifiers) && ignoreStatic) }
				.filter { MethodHelper.sizeBiggerThan(2, it) }.each {


			def parent = NodeHelper.findDeclaringClass(it)
			parent.ifPresent {
				def type = new ClassOrInterfaceType(((ClassOrInterfaceDeclaration) parent.get()).name)
				currentClassName = innerClassesHandler.getUnqualifiedNameForInnerClass(type)
			}

			def allCalls = MethodHelper.getAllMethodInvocations(it)

			def parameters = MethodHelper.extractParameters(it).stream()
					.map { VariableHelper.toJpalFromParameter(it) }.collect(Collectors.toSet())
			def variables = VariableHelper.toJpalFromLocales(LocaleVariableHelper.find(it).toList())

			analyzeVariables(it, allCalls, filter.forJavaClasses(variables))
			analyzeVariables(it, allCalls, filter.forJavaClasses(parameters))
			analyzeVariables(it, allCalls, filter.forJavaClasses(fields))

		}
	}

	private analyzeVariables(MethodDeclaration method, int allCalls, Set<JpalVariable> variables) {
		variables.forEach {
			int count = MethodHelper.getAllMethodInvocationsForEntityWithName(it.name, method)
			double factor = calc(count, allCalls)

			if (factor > featureEnvyFactor.threshold) {
				def roundedFactor = (factor * 100).toInteger().toDouble() / 100

				def featureEnvy = new FeatureEnvy(
						method.name, method.declarationAsString, currentClassName,
						it.name, it.type.toString(), it.nature.toString(),
						roundedFactor, featureEnvyFactor.threshold,
						SourcePath.of(path), SourceRange.fromNode(method))

				smells.add(featureEnvy)
			}
		}
	}

	private double calc(int entityCalls, int allCalls) {
		if (allCalls == 0 || allCalls == 1) {
			return 0.0
		}

		def weight = featureEnvyFactor.weight
		return weight * (entityCalls / allCalls) + (1 - weight) * (1 - Math.pow(featureEnvyFactor.base, entityCalls))
	}

}
