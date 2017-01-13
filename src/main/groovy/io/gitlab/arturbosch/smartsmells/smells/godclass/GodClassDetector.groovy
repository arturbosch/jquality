package io.gitlab.arturbosch.smartsmells.smells.godclass

import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell

/**
 * @author artur
 */
class GodClassDetector extends Detector<GodClass> {

	int wmcThreshold
	int atfdThreshold
	double tccThreshold

	GodClassDetector(
			int wmcThreshold = Defaults.WEIGHTED_METHOD_COUNT,
			int atfdThreshold = Defaults.ACCESS_TO_FOREIGN_DATA,
			double tccThreshold = Defaults.TIED_CLASS_COHESION) {

		this.wmcThreshold = wmcThreshold
		this.atfdThreshold = atfdThreshold
		this.tccThreshold = tccThreshold
	}

	@Override
	protected Visitor getVisitor() {
		return new GodClassVisitor(atfdThreshold, wmcThreshold, tccThreshold)
	}

	@Override
	Smell getType() {
		return Smell.GOD_CLASS
	}
}
