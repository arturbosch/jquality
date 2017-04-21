package io.gitlab.arturbosch.smartsmells.api

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam
import io.gitlab.arturbosch.smartsmells.metrics.Metric

import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
@CompileStatic
trait CompositeMetricRaiser {

	List<MetricRaiser> raisers = Collections.emptyList()

	abstract List<Metric> raise(ClassOrInterfaceDeclaration aClass)

	void init(@ClosureParams(FirstParam.FirstGenericType.class) Closure<List<MetricRaiser>> initialization) {
		initialization.call(raisers)
	}
}

@CompileStatic
class SimpleCompositeMetricRaiser implements CompositeMetricRaiser {

	SimpleCompositeMetricRaiser(List<MetricRaiser> raisers) {
		this.raisers = raisers
	}

	List<Metric> raise(ClassOrInterfaceDeclaration aClass) {
		return raisers.collect { it.raise(aClass) }
	}
}

@CompileStatic
class CombinedCompositeMetricRaiser implements CompositeMetricRaiser {

	List<CompositeMetricRaiser> compositions

	CombinedCompositeMetricRaiser(List<CompositeMetricRaiser> compositions) {
		this.compositions = compositions
	}

	@Override
	List<Metric> raise(ClassOrInterfaceDeclaration aClass) {
		return compositions.stream()
				.flatMap { it.raise(aClass).stream() }
				.collect(Collectors.toList())
	}

	@Override
	void init(@ClosureParams(FirstParam.FirstGenericType.class) Closure<List<MetricRaiser>> initialization) {
		compositions.each { it.init(initialization) }
	}
}