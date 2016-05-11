package com.gitlab.artismarti.smartsmells.longparam

import com.gitlab.artismarti.smartsmells.config.Defaults
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class LongParameterListDetector extends Detector<LongParameterList> {

	private int threshold

	LongParameterListDetector(int threshold = Defaults.LONG_PARAMETER_LIST) {
		this.threshold = threshold
	}

	@Override
	protected Visitor getVisitor(Path path) {
		new LongParameterListVisitor(threshold, path)
	}

	@Override
	Smell getType() {
		return Smell.LONG_PARAM
	}
}
