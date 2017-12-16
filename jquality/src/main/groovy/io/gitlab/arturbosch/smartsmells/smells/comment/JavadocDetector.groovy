package io.gitlab.arturbosch.smartsmells.smells.comment

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell

/**
 * @author Artur Bosch
 */
@CompileStatic
class JavadocDetector extends Detector<CommentSmell> {

	boolean onlyInterfaces

	JavadocDetector(boolean onlyInterfaces = Defaults.ONLY_INTERFACES) {
		this.onlyInterfaces = onlyInterfaces
	}

	@Override
	protected Visitor getVisitor() {
		return new JavadocVisitor(onlyInterfaces)
	}

	@Override
	Smell getType() {
		return Smell.COMMENT
	}
}
