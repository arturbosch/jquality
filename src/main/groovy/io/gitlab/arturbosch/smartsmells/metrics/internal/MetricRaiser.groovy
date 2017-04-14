package io.gitlab.arturbosch.smartsmells.metrics.internal

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.metrics.Metrics

/**
 * @author Artur Bosch
 */
@CompileStatic
trait MetricRaiser {
	Resolver resolver

	abstract Metric raise(ClassOrInterfaceDeclaration aClass)

	void setResolver(Resolver resolver) {
		this.resolver = resolver
	}
}

@CompileStatic
class SimpleCompositeMetricRaiser implements CompositeMetricRaiser {
	final List<MetricRaiser> raisers

	SimpleCompositeMetricRaiser(List<MetricRaiser> raisers) {
		this.raisers = raisers
	}

	List<Metric> raise(ClassOrInterfaceDeclaration aClass) {
		return raisers.collect { it.raise(aClass) }
	}
}

@CompileStatic
class WMC implements MetricRaiser {
	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of("WeightedMethodCount", Metrics.wmc(aClass))
	}
}

@CompileStatic
class TCC implements MetricRaiser {
	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of("TiedClassCohesion", Metrics.tcc(aClass))
	}
}

@CompileStatic
class ATFD implements MetricRaiser {
	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of("AccessToForeignData", Metrics.atfd(aClass))
	}
}

@CompileStatic
class MCCabe implements MetricRaiser {
	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		def methods = aClass.getNodesByType(MethodDeclaration.class)
		int methodCount = methods.isEmpty() ? 1 : methods.size()
		def mccSum = methods.stream().mapToInt { Metrics.mcCabe(aClass) }.sum()
		def averageMCC = mccSum / methodCount
		return Metric.of("MCCabe", averageMCC.toDouble())
	}
}

@CompileStatic
class LOC implements MetricRaiser {
	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of("LinesOfCode", Metrics.loc(aClass))
	}
}

@CompileStatic
class SLOC implements MetricRaiser {
	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of("SourceLinesOfCode", Metrics.sloc(aClass))
	}
}

@CompileStatic
class NOA implements MetricRaiser {
	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of("NumberOfAttributes", Metrics.noa(aClass))
	}
}

@CompileStatic
class NOM implements MetricRaiser {
	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of("SourceLinesOfCode", Metrics.nom(aClass))
	}
}

@CompileStatic
class CC implements MetricRaiser {
	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of("CountClasses", Metrics.cc(aClass, resolver))
	}
}

@CompileStatic
class CM implements MetricRaiser {
	@Override
	Metric raise(ClassOrInterfaceDeclaration aClass) {
		return Metric.of("CountMethods", Metrics.cm(aClass, resolver))
	}
}