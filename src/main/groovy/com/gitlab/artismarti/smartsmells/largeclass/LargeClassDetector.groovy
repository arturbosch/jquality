package com.gitlab.artismarti.smartsmells.largeclass

import com.gitlab.artismarti.smartsmells.config.Defaults
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class LargeClassDetector extends Detector<LargeClass> {

	private int sizeThreshold

	LargeClassDetector(int sizeThreshold = Defaults.LARGE_CLASS) {
		this.sizeThreshold = sizeThreshold
	}

	@Override
	protected Visitor getVisitor(Path path) {
		return new LargeClassVisitor(path, sizeThreshold)
	}

	@Override
	Smell getType() {
		return Smell.LARGE_CLASS
	}
}
