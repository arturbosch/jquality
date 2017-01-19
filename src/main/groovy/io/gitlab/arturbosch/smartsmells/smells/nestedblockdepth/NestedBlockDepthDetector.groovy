package io.gitlab.arturbosch.smartsmells.smells.nestedblockdepth

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell

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
