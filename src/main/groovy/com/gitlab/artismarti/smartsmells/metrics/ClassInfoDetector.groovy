package com.gitlab.artismarti.smartsmells.metrics

import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.config.Smell

import java.nio.file.Path

/**
 * @author artur
 */
class ClassInfoDetector extends Detector<ClassInfo> {

	private boolean skipCC_CM

	ClassInfoDetector(boolean skipCC_CM = false) {
		this.skipCC_CM = skipCC_CM
	}

	@Override
	protected Visitor getVisitor(Path path) {
		return new ClassInfoVisitor(path, skipCC_CM)
	}

	@Override
	Smell getType() {
		return Smell.CLASS_INFO
	}
}
