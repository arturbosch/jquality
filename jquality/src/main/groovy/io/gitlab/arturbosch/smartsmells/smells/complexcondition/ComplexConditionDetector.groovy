package io.gitlab.arturbosch.smartsmells.smells.complexcondition

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell

/**
 * @author Artur Bosch
 */
@CompileStatic
class ComplexConditionDetector extends Detector<ComplexCondition> {

	private int threshold

	ComplexConditionDetector(int threshold = Defaults.COMPLEX_CONDITION) {
		this.threshold = threshold
	}

	@Override
	protected Visitor getVisitor() {
		return new ComplexConditionVisitor(threshold)
	}

	@Override
	Smell getType() {
		return Smell.COMPLEX_CONDITION
	}

}
