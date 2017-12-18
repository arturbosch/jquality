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

	private Set<String> packageNames
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
		packageNames = resolver.storage.getStoredPackageNames()
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
				.filter { !it.static }
				.collect(Collectors.toList())

		fields = VariableHelper.toJpalFromFields(filteredFields)

		analyzeMethods(NodeHelper.findMethods(n))
	}

	private analyzeMethods(List<MethodDeclaration> methods) {
		for (MethodDeclaration method : methods) {
			if (!MethodHelper.isAnonymousMethod(method)
					&& notStaticIfIgnored(method)
					&& MethodHelper.sizeBiggerThan(2, method)) {
				analyzeMethod(method)
			}
		}
	}

	private void analyzeMethod(MethodDeclaration method) {
		NodeHelper.findDeclaringClass(method).ifPresent {
			currentClass = (ClassOrInterfaceDeclaration) it
			def type = new ClassOrInterfaceType(currentClass.nameAsString)
			currentClassName = innerClassesHandler.getUnqualifiedNameForInnerClass(type)
		}

		def allCalls = MethodHelper.getAllMethodInvocations(method)

		def parameters = MethodHelper.extractParameters(method)
				.stream()
				.map { VariableHelper.toJpalFromParameter(it) }
				.collect(Collectors.toSet())
		def variables = LocaleVariableHelper.find(method)
				.stream()
				.map { VariableHelper.toJpalFromLocale(it) }
				.flatMap { it.stream() }
				.collect(Collectors.toSet())

		analyzeVariables(method, allCalls, javaClassFilter.filter(variables))
		analyzeVariables(method, allCalls, javaClassFilter.filter(parameters))
		analyzeVariables(method, allCalls, javaClassFilter.filter(fields))
	}

	private boolean notStaticIfIgnored(MethodDeclaration method) {
		!(method.modifiers.contains(Modifier.STATIC) && ignoreStatic)
	}

	private boolean isSameProjectDifferentClass(JpalVariable it) {
		if (!(it.type instanceof ClassOrInterfaceType)) return false
		def type = it.type as ClassOrInterfaceType
		return type.nameAsString != currentClassName && notInheritedOrOtherProject(currentClass, type)
	}

	private boolean notInheritedOrOtherProject(ClassOrInterfaceDeclaration aClass, ClassOrInterfaceType checkedType) {
		def qualifiedType = resolver.resolveType(checkedType, info)

		// same project
		def maybeSameProject = resolver.find(qualifiedType)
				.map { it.qualifiedType.onlyPackageName }
				.filter { packageNames.contains(it) }
		if (!maybeSameProject.isPresent()) return false

		// inheritance check
		def ancestors = TypeHelper.findAllAncestors(aClass, resolver, info)
		return ancestors.find { it == qualifiedType } == null
	}

	private analyzeVariables(MethodDeclaration method, int allCalls, Set<JpalVariable> variables) {
		for (JpalVariable variable : variables) {
			if (isSameProjectDifferentClass(variable)) {
				int count = MethodHelper.getAllMethodInvocationsForEntityWithName(variable.name, method)
				double factor = calc(count, allCalls)

				if (factor > featureEnvyFactor.threshold) {
					def roundedFactor = (factor * 100).toInteger().toDouble() / 100

					def featureEnvy = new FeatureEnvy(
							method.nameAsString, method.declarationAsString, currentClassName,
							variable.name, Printer.toString(variable.type), variable.nature.toString(),
							roundedFactor, featureEnvyFactor.threshold,
							SourceRange.fromNode(method), SourcePath.of(info), ElementTarget.METHOD)

					smells.add(featureEnvy)
				}
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
