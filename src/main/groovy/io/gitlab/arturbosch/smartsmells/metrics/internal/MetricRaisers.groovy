package io.gitlab.arturbosch.smartsmells.metrics.internal

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.MetricRaiser
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.metrics.Metrics
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethodVisitor

@CompileStatic
class NOA implements MetricRaiser {

	public static final String NUMBER_OF_ATTRIBUTES = "NumberOfAttributes"

	@Override
	String metricName() {
		return NUMBER_OF_ATTRIBUTES
	}

	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of(NUMBER_OF_ATTRIBUTES, Metrics.noa(aClass))
	}
}

@CompileStatic
class NOM implements MetricRaiser {

	static final String NUMBER_OF_METHODS = "NumberOfMethods"

	@Override
	String metricName() {
		return NUMBER_OF_METHODS
	}

	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of(NUMBER_OF_METHODS, Metrics.nom(aClass))
	}
}

