package io.gitlab.arturbosch.smartsmells.smells.comment

import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.config.Smell

import java.nio.file.Path

/**
 * Detector finds orphan comments within methods or not very useful comments over private/package-private methods.
 * Comments are considered as a smell because they shade poor code which should be rewritten instead.
 *
 * @author artur
 */
class CommentDetector extends Detector<CommentSmell> {

	@Override
	protected CommentVisitor getVisitor(Path path) {
		return new CommentVisitor(path)
	}

	@Override
	Smell getType() {
		return Smell.COMMENT
	}
}
