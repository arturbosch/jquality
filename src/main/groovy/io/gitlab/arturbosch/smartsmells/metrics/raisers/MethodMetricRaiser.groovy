package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.Metric

/**
 * @author Artur Bosch
 */
@CompileStatic
interface MethodMetricRaiser {

	Metric raise(MethodDeclaration method, Resolver resolver)
}
