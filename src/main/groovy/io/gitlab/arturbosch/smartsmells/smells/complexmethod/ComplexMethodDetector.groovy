package io.gitlab.arturbosch.smartsmells.smells.complexmethod

import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell

import java.nio.file.Path

/**
 * @author artur
 */
class ComplexMethodDetector extends Detector<ComplexMethod> {

	int complexity

	ComplexMethodDetector(int complexityThreshold = Defaults.COMPLEX_METHOD) {
		this.complexity = complexityThreshold
	}

	@Override
	protected Visitor getVisitor(Path path) {
		return new ComplexMethodVisitor(complexity, path)
	}

	@Override
	Smell getType() {
		return Smell.COMPLEX_METHOD
	}
}
