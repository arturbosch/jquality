package com.gitlab.artismarti.smartsmells.dataclass

import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Smell
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
	protected Smell getType() {
		return Smell.DATA_CLASS
	}
}
