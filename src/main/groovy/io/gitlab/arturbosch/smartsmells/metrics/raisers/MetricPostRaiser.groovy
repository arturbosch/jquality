package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo
import io.gitlab.arturbosch.smartsmells.metrics.Metric

import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
@CompileStatic
trait MetricPostRaiser {

	int priority() {
		return 0
	}

	abstract void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver)

	ClassInfo findClassInfo(ClassOrInterfaceDeclaration aClass, CompilationInfo info) {
		return info.getData(FileInfo.KEY).findClassByName(aClass.nameAsString)
	}
}

@CompileStatic
class AMW implements MetricPostRaiser {

	static final String AVERAGE_METHOD_WEIGHT = "AverageMethodWeight"

	@Override
	int priority() {
		return 1
	}

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		def classInfo = findClassInfo(aClass, info)
		int wmc = classInfo.getMetric(WMC.WEIGHTED_METHOD_COUNT)?.value ?: 0
		int nom = classInfo.getMetric(NOM.NUMBER_OF_METHODS)?.value ?: 1
		Metric metric = Metric.of(AVERAGE_METHOD_WEIGHT, (double) wmc / nom)
		classInfo.addMetric(metric)
	}
}

@CompileStatic
class WMC implements MetricPostRaiser {

	static final String WEIGHTED_METHOD_COUNT = "WeightedMethodCount"

	@Override
	void raise(ClassOrInterfaceDeclaration aClass, CompilationInfo info, Resolver resolver) {
		def classInfo = findClassInfo(aClass, info)
		List<Metric> metrics = classInfo.methods.stream()
				.map { it.getMetric(CYCLO.CYCLOMATIC_COMPLEXITY) }
				.filter { it != null }
				.collect(Collectors.toList())
		def wmc = metrics.stream().mapToInt { it.value }.reduce(0) { int first, int second -> first + second }
		classInfo.addMetric(Metric.of(WEIGHTED_METHOD_COUNT, wmc))
	}
}
