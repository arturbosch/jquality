package com.gitlab.artismarti.smartsmells.cycle

import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class CycleDetector extends Detector<Cycle> {

	@Override
	protected Visitor getVisitor(Path path) {
		return new CycleVisitor(path)
	}

}
