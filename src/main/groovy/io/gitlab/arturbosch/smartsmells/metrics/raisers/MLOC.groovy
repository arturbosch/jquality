package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.CallableDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.metrics.internal.LOC
import io.gitlab.arturbosch.smartsmells.metrics.internal.LinesOfCode

/**
 * @author Artur Bosch
 */
@CompileStatic
class MLOC implements CompositeMethodMetricRaiser {

	@Override
	String name() {
		return "MLOC"
	}

	@Override
	Set<Metric> raise(CallableDeclaration method, Resolver resolver) {
		def loc = new LinesOfCode()
		loc.analyze(LOC.NL.split(method.toString()))
		return [Metric.of(LOC.LOC, loc.source + loc.blank + loc.comment),
				Metric.of(LOC.SLOC, loc.source),
				Metric.of(LOC.CLOC, loc.comment),
				Metric.of(LOC.LLOC, loc.logical),
				Metric.of(LOC.BLOC, loc.blank)].toSet()
	}
}
