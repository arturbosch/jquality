package io.gitlab.arturbosch.smartsmells.metrics.internal

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.MetricRaiser
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.metrics.Metrics
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethodVisitor

@CompileStatic
class WMC implements MetricRaiser {

	public static final String WEIGHTED_METHOD_COUNT = "WeightedMethodCount"

	@Override
	String name() {
		return WEIGHTED_METHOD_COUNT
	}

	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of(WEIGHTED_METHOD_COUNT, Metrics.wmc(aClass))
	}
}

@CompileStatic
class TCC implements MetricRaiser {

	public static final String TIED_CLASS_COHESION = "TiedClassCohesion"

	@Override
	String name() {
		return TIED_CLASS_COHESION
	}

	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of(TIED_CLASS_COHESION, Metrics.tcc(aClass))
	}
}

@CompileStatic
class ATFD implements MetricRaiser {

	public static final String ACCESS_TO_FOREIGN_DATA = "AccessToForeignData"

	@Override
	String name() {
		return ACCESS_TO_FOREIGN_DATA
	}

	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of(ACCESS_TO_FOREIGN_DATA, Metrics.atfd(aClass))
	}
}

@CompileStatic
class AMW implements MetricRaiser {

	public static final String AVERAGE_METHOD_WEIGHT = "AverageMethodWeight"

	@Override
	String name() {
		return AVERAGE_METHOD_WEIGHT
	}

	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		def methods = aClass.getChildNodesByType(MethodDeclaration.class)
		def average = methods.stream()
				.mapToInt { Metrics.mcCabe(aClass) }
				.average()
				.orElse(0.0d)
		return Metric.of(AVERAGE_METHOD_WEIGHT, average)
	}
}

@CompileStatic
class NOA implements MetricRaiser {

	public static final String NUMBER_OF_ATTRIBUTES = "NumberOfAttributes"

	@Override
	String name() {
		return NUMBER_OF_ATTRIBUTES
	}

	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of(NUMBER_OF_ATTRIBUTES, Metrics.noa(aClass))
	}
}

@CompileStatic
class NOM implements MetricRaiser {

	public static final String NUMBER_OF_METHODS = "NumberOfMethods"

	@Override
	String name() {
		return NUMBER_OF_METHODS
	}

	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of(NUMBER_OF_METHODS, Metrics.nom(aClass))
	}
}

@CompileStatic
class CC implements MetricRaiser {

	public static final String COUNT_CLASSES = "CountClasses"

	@Override
	String name() {
		return COUNT_CLASSES
	}

	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of(COUNT_CLASSES, Metrics.cc(aClass, resolver))
	}
}

@CompileStatic
class CM implements MetricRaiser {

	public static final String COUNT_METHODS = "CountMethods"

	@Override
	String name() {
		return COUNT_METHODS
	}

	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of(COUNT_METHODS, Metrics.cm(aClass, resolver))
	}
}

@CompileStatic
class LM implements MetricRaiser {

	public static final String LONG_METHOD = 'LongMethod'

	@Override
	String name() {
		return LONG_METHOD
	}

	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		def methods = aClass.getChildNodesByType(MethodDeclaration.class)
		def average = methods.stream()
				.mapToInt { LongMethodVisitor.bodyLength(it) }
				.average()

		return Metric.of(LONG_METHOD, average.orElse(0.0d))
	}
}

@CompileStatic
class LPL implements MetricRaiser {

	public static final String LONG_PARAMETER_LIST = 'LongParameterList'

	@Override
	String name() {
		return LONG_PARAMETER_LIST
	}

	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		def methods = aClass.getChildNodesByType(MethodDeclaration.class)
		def average = methods.stream()
				.mapToInt { it.parameters.size() }
				.average()

		return Metric.of(LONG_PARAMETER_LIST, average.orElse(0.0d))
	}
}
