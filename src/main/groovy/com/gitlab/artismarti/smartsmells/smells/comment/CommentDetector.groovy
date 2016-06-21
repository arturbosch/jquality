package com.gitlab.artismarti.smartsmells.smells.comment

import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.config.Smell

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
