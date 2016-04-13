package com.gitlab.artismarti.smartsmells.longmethod

import com.gitlab.artismarti.smartsmells.common.Defaults
import com.gitlab.artismarti.smartsmells.common.Detector
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

}
