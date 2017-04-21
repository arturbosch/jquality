package io.gitlab.arturbosch.smartsmells.smells.cycle

import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell

/**
 * @author artur
 */
class CycleDetector extends Detector<Cycle> {

	@Override
	protected Visitor getVisitor() {
		return new CycleVisitor()
	}

	@Override
	Smell getType() {
		return Smell.CYCLE
	}
}
