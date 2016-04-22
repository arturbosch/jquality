package com.gitlab.artismarti.smartsmells.middleman

import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class MiddleManDetector extends Detector<MiddleMan> {

	@Override
	protected Visitor getVisitor(Path path) {
		return new MiddleManVisitor(path)
	}

}
