package io.gitlab.arturbosch.smartsmells.smells.middleman

import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell

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
