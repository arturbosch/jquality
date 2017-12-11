package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.TypeHelper
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo
import io.gitlab.arturbosch.smartsmells.metrics.MethodInfo
import io.gitlab.arturbosch.smartsmells.metrics.Metric

import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
@CompileStatic
trait MetricPreListener {

	int priority() {
		return 0
	}

	abstract void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver)

	/**
	 * @return the class info for given class or null
	 */
	ClassInfo findClassInfo(ClassOrInterfaceDeclaration aClass, CompilationInfo info) {
		return info.getData(FileInfo.KEY)?.findClassByName(aClass.nameAsString)
	}
}

@CompileStatic
class NAS implements MetricPreListener {

	static final String NUMBER_OF_ADDED_SERVICES = "NumberofAddedServices"
	static final String PERCENTAGE_OF_NEWLY_ADDED_SERVICES = "PercentageofNewlyAddedServices"

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		def classInfo = findClassInfo(aClass, info)
		if (classInfo) {
			Collection<MethodInfo> ancestorMethods = TypeHelper.findAllAncestors(aClass, resolver).stream()
					.flatMap { QualifiedType ancestorType -> resolveAncestorMethods(resolver, ancestorType).stream() }
					.collect()
			Set<MethodInfo> methodInfos = classInfo.methods
			Metric pnas = raisePNAS(ancestorMethods, methodInfos)
			Metric nas = raiseNAS(ancestorMethods, methodInfos)
			classInfo.addMetric(nas)
			classInfo.addMetric(pnas)
		}
	}

	private static Metric raisePNAS(Collection<MethodInfo> ancestorMethods, Set<MethodInfo> methodInfos) {
		def publicAncestorServices = ancestorMethods.stream()
				.filter { it.declaration.isPublic() && !it.declaration.isStatic() }
				.map { it.declarationString }
				.collect()
		def publicServices = methodInfos.stream()
				.filter { it.declaration.isPublic() && !it.declaration.isStatic() }
				.map { it.declarationString }
				.collect()

		int publicServiceSize = publicServices.findAll { !(it in publicAncestorServices) }.size()
		int totalPublicServices = (publicServices + publicAncestorServices).toSet().size()
		def value = totalPublicServices == 0 ? 0.0d : (double) publicServiceSize / totalPublicServices

		Metric.of(PERCENTAGE_OF_NEWLY_ADDED_SERVICES, value)
	}

	private static Metric raiseNAS(Collection<MethodInfo> ancestorMethods, Set<MethodInfo> methodInfos) {
		Set<String> ancestorNames = ancestorMethods.stream()
				.map { MethodInfo mi -> mi.declarationString }
				.collect(Collectors.toSet())
		def addedServices = methodInfos
				.findAll { !(it.declarationString in ancestorNames) }
				.size()
		Metric.of(NUMBER_OF_ADDED_SERVICES, addedServices)
	}

	private Set<MethodInfo> resolveAncestorMethods(Resolver resolver, QualifiedType ancestorType) {
		resolver.find(ancestorType)
				.map { CompilationInfo ancestorInfo -> extractMethodInfos(ancestorInfo, ancestorType) }
				.orElse(Collections.emptySet() as Set<MethodInfo>)
	}

	private Set<MethodInfo> extractMethodInfos(CompilationInfo ancestorInfo, QualifiedType ancestorType) {
		ancestorInfo.getTypeDeclarationByQualifier(ancestorType)
				.filter { it instanceof ClassOrInterfaceDeclaration }
				.map { findClassInfo(it as ClassOrInterfaceDeclaration, ancestorInfo) }
				.map { it.methods }
				.orElse(Collections.emptySet() as Set<MethodInfo>)
	}
}
