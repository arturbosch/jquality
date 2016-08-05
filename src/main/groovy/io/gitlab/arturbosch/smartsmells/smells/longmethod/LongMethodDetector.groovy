package io.gitlab.arturbosch.smartsmells.smells.longmethod

import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell

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
