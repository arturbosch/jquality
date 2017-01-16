package io.gitlab.arturbosch.smartsmells.smells.statechecking

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell;

/**
 * @author Artur Bosch
 */
@CompileStatic
class StateCheckingDetector extends Detector<StateChecking> {

	@Override
	protected Visitor getVisitor() {
		return new StateCheckingVisitor()
	}

	@Override
	Smell getType() {
		return Smell.STATE_CHECKING
	}
}
