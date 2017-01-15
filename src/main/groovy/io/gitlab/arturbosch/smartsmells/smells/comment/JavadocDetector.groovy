package io.gitlab.arturbosch.smartsmells.smells.comment

import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell

/**
 * @author Artur Bosch
 */
@CompileStatic
class JavadocDetector extends Detector<CommentSmell> {

	@Override
	protected Visitor getVisitor() {
		return new JavadocVisitor()
	}

	@Override
	Smell getType() {
		return Smell.COMMENT
	}
}
