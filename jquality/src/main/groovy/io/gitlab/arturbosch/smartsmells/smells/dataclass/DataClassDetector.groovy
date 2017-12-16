package io.gitlab.arturbosch.smartsmells.smells.dataclass

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell

/**
 * @author Artur Bosch
 */
@CompileStatic
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
