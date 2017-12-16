package io.gitlab.arturbosch.smartsmells.smells.cycle

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell

/**
 * @author Artur Bosch
 */
@CompileStatic
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
