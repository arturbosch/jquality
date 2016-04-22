package com.gitlab.artismarti.smartsmells.comment

import com.gitlab.artismarti.smartsmells.common.Detector

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
}
