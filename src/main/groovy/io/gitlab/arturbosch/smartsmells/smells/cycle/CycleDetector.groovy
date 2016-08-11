package io.gitlab.arturbosch.smartsmells.smells.cycle

import io.gitlab.arturbosch.jpal.core.CompilationTree
import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell

import java.nio.file.Path

/**
 * @author artur
 */
class CycleDetector extends Detector<Cycle> {

	/**
	 * Use this constructor if running from detector facade.
	 */
	CycleDetector() {
	}

	/**
	 * Attention!!! Only use this constructor if starting this detector in single mode.
	 *
	 * @param startPath base path for compilation tree
	 */
	CycleDetector(Path startPath) {
		CompilationTree.registerRoot(startPath)
	}


	@Override
	protected Visitor getVisitor(Path path) {
		return new CycleVisitor(path)
	}

	@Override
	Smell getType() {
		return Smell.CYCLE
	}
}
