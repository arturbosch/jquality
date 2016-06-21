package com.gitlab.artismarti.smartsmells.smells.dataclass

import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class DataClassDetector extends Detector<DataClass> {

	@Override
	protected Visitor getVisitor(Path path) {
		new DataClassVisitor(path)
	}

	@Override
	Smell getType() {
		return Smell.DATA_CLASS
	}
}
