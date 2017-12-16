package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.MethodHelper
import io.gitlab.arturbosch.jpal.ast.TypeHelper
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo
import io.gitlab.arturbosch.smartsmells.metrics.MethodInfo
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.metrics.Metrics
import io.gitlab.arturbosch.smartsmells.metrics.internal.LinesOfCode
import io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery.CMCCMetrics

import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
@CompileStatic
trait PreClassListener {

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
class NAS implements PreClassListener {

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
				.filter { it.declaration instanceof MethodDeclaration }
				.filter { it.declaration.isPublic() && !it.declaration.isStatic() }
				.filter { !MethodHelper.isGetterOrSetter(it.declaration as MethodDeclaration) }
				.map { it.declarationString }
				.collect()
		def publicServices = methodInfos.stream()
				.filter { it.declaration instanceof MethodDeclaration }
				.filter { it.declaration.isPublic() && !it.declaration.isStatic() }
				.filter { !MethodHelper.isGetterOrSetter(it.declaration as MethodDeclaration) }
				.map { it.declarationString }
				.collect()

		int publicServiceSize = publicServices.findAll { !(it in publicAncestorServices) }.size()
		int totalPublicServices = (publicServices + publicAncestorServices).toSet().size()
		def value = totalPublicServices == 0 ? 0.0d : (double) publicServiceSize / totalPublicServices

		Metric.of(PERCENTAGE_OF_NEWLY_ADDED_SERVICES, value)
	}

	private static Metric raiseNAS(Collection<MethodInfo> ancestorMethods, Set<MethodInfo> methodInfos) {
		Set<String> ancestorNames = ancestorMethods.stream()
				.filter { it.declaration instanceof MethodDeclaration }
				.filter { it.declaration.isPublic() && !it.declaration.isStatic() }
				.filter { !MethodHelper.isGetterOrSetter(it.declaration as MethodDeclaration) }
				.map { it.declarationString }
				.collect(Collectors.toSet())
		int addedServices = methodInfos.stream()
				.filter { it.declaration instanceof MethodDeclaration }
				.filter { it.declaration.isPublic() && !it.declaration.isStatic() }
				.filter { !MethodHelper.isGetterOrSetter(it.declaration as MethodDeclaration) }
				.filter { !(it.declarationString in ancestorNames) }
				.filter { !it.declaration.getAnnotationByName("Override").isPresent() }
				.count() as int
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

@CompileStatic
class CC_CM implements PreClassListener {

	static final String COUNT_CLASSES = "CountClasses"
	static final String COUNT_METHODS = "CountMethods"

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		def pair = CMCCMetrics.raise(aClass, resolver)
		def (int cc, int cm) = [pair.a, pair.b]
		def classInfo = findClassInfo(aClass, info)
		classInfo?.addMetric(Metric.of(COUNT_CLASSES, cc))
		classInfo?.addMetric(Metric.of(COUNT_METHODS, cm))
	}
}

@CompileStatic
class TCC implements PreClassListener {

	static final String TIED_CLASS_COHESION = "TiedClassCohesion"

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		findClassInfo(aClass, info)?.addMetric(Metric.of(TIED_CLASS_COHESION, Metrics.tcc(aClass)))
	}
}

@CompileStatic
class ATFD implements PreClassListener {

	static final String ACCESS_TO_FOREIGN_DATA = "AccessToForeignData"

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		findClassInfo(aClass, info)?.addMetric(Metric.of(ACCESS_TO_FOREIGN_DATA, Metrics.atfd(aClass)))
	}
}

@CompileStatic
class LOC implements PreClassListener {

	static final String LOC = "LinesOfCode"
	static final String SLOC = "SourceLinesOfCode"
	static final String CLOC = "CommentsLinesOfCode"
	static final String LLOC = "LogicalLinesOfCode"
	static final String BLOC = "BlankLinesOfCode"

	static final Pattern NL = Pattern.compile("\n")

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		def classInfo = findClassInfo(aClass, info)
		if (classInfo) {
			def content = NL.split(aClass.toString())
			def loc = new LinesOfCode()
			loc.analyze(content)
			[Metric.of(LOC, loc.source + loc.blank + loc.comment),
			 Metric.of(SLOC, loc.source),
			 Metric.of(CLOC, loc.comment),
			 Metric.of(LLOC, loc.logical),
			 Metric.of(BLOC, loc.blank)].each {
				classInfo.addMetric(it)
			}
		}
	}
}

@CompileStatic
class NOA implements PreClassListener {

	static final String NUMBER_OF_ATTRIBUTES = "NumberOfAttributes"
	static final String NUMBER_OF_PUBLIC_ATTRIBUTES = "NumberOfPublicAttributes"

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		def classInfo = findClassInfo(aClass, info)

		if (classInfo) {
			int noa = 0
			int nopa = 0
			for (FieldDeclaration field in aClass.fields) {
				if (!field.isStatic()) {
					def vars = field.variables.size()
					if (field.isPublic()) {
						nopa += vars
					}
					noa += vars
				}
			}
			classInfo.addMetric(Metric.of(NUMBER_OF_ATTRIBUTES, noa))
			classInfo.addMetric(Metric.of(NUMBER_OF_PUBLIC_ATTRIBUTES, nopa))
		}
	}
}

@CompileStatic
class NOM implements PreClassListener {

	static final String NUMBER_OF_METHODS = "NumberOfMethods"
	static final String NUMBER_OF_PUBLIC_METHODS = "NumberOfPublicMethods"

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		def classInfo = findClassInfo(aClass, info)

		if (classInfo) {
			int noa = 0
			int nopa = 0
			for (MethodDeclaration method in aClass.methods) {
				if (!method.isStatic()) {
					if (method.isPublic()) {
						nopa += 1
					}
					noa += 1
				}
			}
			classInfo.addMetric(Metric.of(NUMBER_OF_METHODS, noa))
			classInfo.addMetric(Metric.of(NUMBER_OF_PUBLIC_METHODS, nopa))
		}
	}
}
