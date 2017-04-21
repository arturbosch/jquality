package io.gitlab.arturbosch.smartsmells.api

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.Metric

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