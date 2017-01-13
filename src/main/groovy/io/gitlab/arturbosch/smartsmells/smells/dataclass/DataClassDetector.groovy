package io.gitlab.arturbosch.smartsmells.smells.dataclass

import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell

import java.nio.file.Path

/**
 * @author artur
 */
class DataClassDetector extends Detector<DataClass> {

	@Override
	protected Visitor getVisitor() {
		return new DataClassVisitor()
	}

	@Override
	Smell getType() {
		return Smell.DATA_CLASS
	}
}
