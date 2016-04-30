package com.gitlab.artismarti.smartsmells.cycle

import com.gitlab.artismarti.smartsmells.common.CompilationTree
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Visitor

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

}
