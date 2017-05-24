package io.gitlab.arturbosch.smartsmells.smells.featureenvy

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.LocaleVariableHelper
import io.gitlab.arturbosch.jpal.ast.MethodHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.TypeHelper
import io.gitlab.arturbosch.jpal.ast.VariableHelper
import io.gitlab.arturbosch.jpal.ast.custom.JpalVariable
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.jpal.resolution.nested.InnerClassesHandler
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

import java.util.stream.Collectors

/**
 * @author artur
 */
class FeatureEnvyVisitor extends Visitor<FeatureEnvy> {

	private FeatureEnvyFactor featureEnvyFactor

	private Set<JpalVariable> fields

	private String currentClassName
	private ClassOrInterfaceDeclaration currentClass
	private JavaClassFilter javaClassFilter
	private InnerClassesHandler innerClassesHandler

	private boolean ignoreStatic
	private Resolver resolver

	FeatureEnvyVisitor(FeatureEnvyFactor factor, boolean ignoreStatic = false) {
		this.featureEnvyFactor = factor
		this.ignoreStatic = ignoreStatic
	}

	@Override
	void visit(CompilationUnit n, Resolver resolver) {
		this.resolver = resolver
		javaClassFilter = new JavaClassFilter(info, resolver)
		innerClassesHandler = info.data.innerClassesHandler
		super.visit(n, resolver)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver resolver) {

		n.getChildNodesByType(ClassOrInterfaceDeclaration.class)
				.each { visit(it, null) }

		if (ClassHelper.isEmptyBody(n)) return
		if (ClassHelper.hasNoMethods(n)) return

		currentClass = n
		currentClassName = n.name

		def filteredFields = NodeHelper.findFields(n).stream()
				.filter { ClassHelper.inClassScope(it, currentClassName) }
				.collect(Collectors.toList())

		fields = VariableHelper.toJpalFromFields(filteredFields)

		analyzeMethods(NodeHelper.findMethods(n))
	}

	private analyzeMethods(List<MethodDeclaration> methods) {
		MethodHelper.filterAnonymousMethods(methods)
				.stream()
				.filter { !(it.modifiers.contains(Modifier.STATIC) && ignoreStatic) }
				.filter { MethodHelper.sizeBiggerThan(2, it) }.each { method ->

			NodeHelper.findDeclaringClass(method).ifPresent {
				currentClass = (ClassOrInterfaceDeclaration) it
				def type = new ClassOrInterfaceType(currentClass.nameAsString)
				currentClassName = innerClassesHandler.getUnqualifiedNameForInnerClass(type)
			}

			def allCalls = MethodHelper.getAllMethodInvocations(method)

			def parameters = MethodHelper.extractParameters(method).stream()
					.map { VariableHelper.toJpalFromParameter(it) }
					.collect(Collectors.toSet())
			def variables = VariableHelper.toJpalFromLocales(
					LocaleVariableHelper.find(method).toList()
			)

			analyzeVariables(method, allCalls, javaClassFilter.filter(variables))
			analyzeVariables(method, allCalls, javaClassFilter.filter(parameters))
			analyzeVariables(method, allCalls, javaClassFilter.filter(fields))

		}
	}

	private boolean notThisClass(JpalVariable it) {
		if (!(it.type instanceof ClassOrInterfaceType)) return false
		def type = it.type as ClassOrInterfaceType
		return type.nameAsString != currentClassName && notInherited(currentClass, type)
	}

	private boolean notInherited(ClassOrInterfaceDeclaration aClass, ClassOrInterfaceType checkedType) {
		def qualifiedType = resolver.resolveType(checkedType, info)
		def ancestors = TypeHelper.findAllAncestors(aClass, resolver, info)
		return ancestors.find { it == qualifiedType } == null
	}

	private analyzeVariables(MethodDeclaration method, int allCalls, Set<JpalVariable> variables) {
		variables.stream()
				.filter { notThisClass(it) }
				.forEach {
			int count = MethodHelper.getAllMethodInvocationsForEntityWithName(it.name, method)
			double factor = calc(count, allCalls)

			if (factor > featureEnvyFactor.threshold) {
				def roundedFactor = (factor * 100).toInteger().toDouble() / 100

				def featureEnvy = new FeatureEnvy(
						method.nameAsString, method.declarationAsString, currentClassName,
						it.name, it.type.toString(Printer.NO_COMMENTS), it.nature.toString(),
						roundedFactor, featureEnvyFactor.threshold,
						SourceRange.fromNode(method), SourcePath.of(info), ElementTarget.METHOD)

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
