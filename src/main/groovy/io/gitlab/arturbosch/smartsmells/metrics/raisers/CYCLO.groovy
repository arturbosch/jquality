package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.metrics.internal.AMW

/**
 * @author Artur Bosch
 */
@CompileStatic
class CYCLO implements MethodMetricRaiser {

	@Override
	Metric raise(MethodDeclaration method, Resolver resolver) {
		new AMW()
		return null
	}
}
