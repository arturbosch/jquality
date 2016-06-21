package com.gitlab.artismarti.smartsmells.smells.middleman

import com.gitlab.artismarti.smartsmells.config.Defaults
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path
/**
 * @author artur
 */
class MiddleManDetector extends Detector<MiddleMan> {

	private MiddleManVisitor.MMT threshold

	MiddleManDetector(MiddleManVisitor.MMT threshold = Defaults.MIDDLE_MAN_THRESHOLD) {
		this.threshold = threshold
	}

	@Override
	protected Visitor getVisitor(Path path) {
		return new MiddleManVisitor(path, threshold)
	}

	@Override
	Smell getType() {
		return Smell.MIDDLE_MAN
	}
}
