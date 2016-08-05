package io.gitlab.arturbosch.smartsmells.smells.largeclass

import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell

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
