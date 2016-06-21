package com.gitlab.artismarti.smartsmells.smells.longmethod

import com.gitlab.artismarti.smartsmells.config.Defaults
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class LongMethodDetector extends Detector<LongMethod> {

	private int threshold

	LongMethodDetector(int threshold = Defaults.LONG_METHOD) {
		this.threshold = threshold
	}

	@Override
	protected Visitor getVisitor(Path path) {
		new LongMethodVisitor(threshold, path)
	}

	@Override
	Smell getType() {
		return Smell.LONG_METHOD
	}
}
