package io.gitlab.arturbosch.smartsmells.smells.complexmethod

import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell

/**
 * @author artur
 */
class ComplexMethodDetector extends Detector<ComplexMethod> {

	int complexity

	ComplexMethodDetector(int complexityThreshold = Defaults.COMPLEX_METHOD) {
		this.complexity = complexityThreshold
	}

	@Override
	protected Visitor getVisitor() {
		return new ComplexMethodVisitor(complexity)
	}

	@Override
	Smell getType() {
		return Smell.COMPLEX_METHOD
	}
}
