package com.gitlab.artismarti.smartsmells.smells.complexmethod

import com.gitlab.artismarti.smartsmells.config.Defaults
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.common.Visitor

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
