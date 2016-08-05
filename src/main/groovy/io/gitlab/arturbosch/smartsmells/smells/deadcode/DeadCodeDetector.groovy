package io.gitlab.arturbosch.smartsmells.smells.deadcode

import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell

import java.nio.file.Path

/**
 * @author artur
 */
class DeadCodeDetector extends Detector<DeadCode> {

	private boolean onlyPrivate

	DeadCodeDetector(boolean onlyPrivate = Defaults.ONLY_PRIVATE_DEAD_CODE) {
		this.onlyPrivate = onlyPrivate
	}

	@Override
	protected Visitor getVisitor(Path path) {
		return new DeadCodeVisitor(path, onlyPrivate)
	}

	@Override
	Smell getType() {
		return Smell.DEAD_CODE
	}
}
