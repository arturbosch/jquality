package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.CallableDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.Metric

/**
 * @author Artur Bosch
 */
@CompileStatic
interface CompositeMethodMetricRaiser {

	String name()

	Set<Metric> raise(CallableDeclaration method, Resolver resolver)
}
