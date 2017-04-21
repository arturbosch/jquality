package io.gitlab.arturbosch.smartsmells.metrics.internal

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.CompositeMetricRaiser
import io.gitlab.arturbosch.smartsmells.metrics.Metric

@CompileStatic
class LongMethodAverageAndDeviation implements CompositeMetricRaiser {

	@Override
	List<Metric> raise(ClassOrInterfaceDeclaration aClass) {
		def methods = aClass.getNodesByType(MethodDeclaration.class)
		int methodCount = methods.isEmpty() ? 1 : methods.size()


		def methodSizes = methods.stream().map { Optional.ofNullable(it.body) }.filter { it.isPresent() }
				.map { it.get() }.mapToInt() { it.map { it.statements.size() }.orElse(0) }
		def amlSum = methodSizes.sum()
		def aml = amlSum / methodCount
		def sml = Math.sqrt(methods.stream().map { Optional.ofNullable(it.body) }.filter { it.isPresent() }
				.map { it.get() }.mapToInt { it.map { it.statements.size() }.orElse(0) }
				.mapToDouble { Math.pow(it - amlSum, 2) }.sum() / methodCount)

		return [Metric.of('LongMethodAverage', aml.toDouble()),
				Metric.of('LongMethodDeviation', sml)]
	}
}

@CompileStatic
class LongParameterListAverageAndDeviation implements CompositeMetricRaiser {

	@Override
	List<Metric> raise(ClassOrInterfaceDeclaration aClass) {
		def methods = aClass.getNodesByType(MethodDeclaration.class)
		int methodCount = methods.isEmpty() ? 1 : methods.size()
		def aplSum = methods.stream().mapToInt { it.parameters.size() }.sum()
		def apl = aplSum / methodCount
		def spl = Math.sqrt(methods.stream().mapToInt { it.parameters.size() }
				.mapToDouble { Math.pow(it - aplSum, 2) }.sum() / methodCount)

		return [Metric.of('LongParameterListAverage', apl.toDouble()),
				Metric.of('LongParameterListDeviation', spl)]
	}
}
