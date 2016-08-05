package io.gitlab.arturbosch.smartsmells.smells.longparam

import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell

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
