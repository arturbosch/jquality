package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.smells.nestedblockdepth.MethodDepthVisitor

/**
 * @author Artur Bosch
 */
@CompileStatic
class MAXNESTING implements MethodMetricRaiser {

	public static final String MAXNESTING = "MAXNESTING"

	@Override
	String name() {
		return MAXNESTING
	}

	@Override
	Metric raise(MethodDeclaration method, Resolver resolver) {
		def visitor = new MethodDepthVisitor()
		visitor.visit(method, resolver)
		return Metric.of(MAXNESTING, visitor.maxDepth)
	}
}
