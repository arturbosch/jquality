package com.gitlab.artismarti.smartsmells.complexmethod

import com.gitlab.artismarti.smartsmells.common.Defaults
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Smell
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
	protected Smell getType() {
		return Smell.COMPLEX_METHOD
	}
}
