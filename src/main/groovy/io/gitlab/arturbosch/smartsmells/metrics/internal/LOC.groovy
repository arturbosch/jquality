package io.gitlab.arturbosch.smartsmells.metrics.internal

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.CompositeMetricRaiser
import io.gitlab.arturbosch.smartsmells.metrics.Metric

import java.util.regex.Pattern

/**
 * @author Artur Bosch
 */
@CompileStatic
class LOC implements CompositeMetricRaiser {

	static final String LOC = "LinesOfCode"
	static final String SLOC = "SourceLinesOfCode"
	static final String CLOC = "CommentsLinesOfCode"
	static final String LLOC = "LogicalLinesOfCode"
	static final String BLOC = "BlankLinesOfCode"

	static final Pattern NL = Pattern.compile("\n")

	@Override
	List<Metric> raise(ClassOrInterfaceDeclaration aClass) {
		def content = NL.split(aClass.toString())
		def loc = new LinesOfCode()
		loc.analyze(content)
		return [Metric.of(LOC, loc.source + loc.blank + loc.comment),
				Metric.of(SLOC, loc.source),
				Metric.of(CLOC, loc.comment),
				Metric.of(LLOC, loc.logical),
				Metric.of(BLOC, loc.blank)]

	}
}
