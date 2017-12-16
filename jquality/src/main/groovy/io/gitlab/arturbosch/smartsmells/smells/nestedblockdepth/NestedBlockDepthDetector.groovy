package io.gitlab.arturbosch.smartsmells.smells.nestedblockdepth

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MAXNESTING
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
@CompileStatic
class NestedBlockDepthDetector extends Detector<NestedBlockDepth> {

	private int depthThreshold

	NestedBlockDepthDetector(int depthThreshold = Defaults.MAX_DEPTH) {
		this.depthThreshold = depthThreshold
	}

	@Override
	protected Visitor getVisitor() {
		return new NestedBlockDepthVisitor(depthThreshold)
	}

	@Override
	Smell getType() {
		return Smell.NESTED_BLOCK_DEPTH
	}
}

@CompileStatic
class NestedBlockDepthVisitor extends Visitor<NestedBlockDepth> {

	private int threshold
	private ClassInfo current

	NestedBlockDepthVisitor(int threshold) {
		this.threshold = threshold
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver arg) {
		current = infoForClass(n)
		super.visit(n, arg)
	}

	@Override
	void visit(MethodDeclaration n, Resolver arg) {
		def methodInfo = current?.getMethodByDeclaration(n)
		def value = methodInfo?.getMetric(MAXNESTING.MAXNESTING)?.value
		if (value > threshold) {
			smells.add(new NestedBlockDepth(n.nameAsString, n.declarationAsString,
					value, threshold, SourceRange.fromNode(n), SourcePath.of(info), ElementTarget.METHOD))
		}
	}
}
