package com.gitlab.artismarti.smartsmells.comment

import com.github.javaparser.Position
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.ModifierSet
import com.github.javaparser.ast.comments.Comment
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.gitlab.artismarti.smartsmells.common.SourcePosition

import java.nio.file.Path

/**
 * Visits all method declaration of a compilation unit and examines them
 * for orphan comments or comments above private/package-private methods.
 *
 * @author artur
 */
class MethodVisitor extends VoidVisitorAdapter<Object> {

	private final
	static String DEFAULT_MESSAGE = "are considered as a smell, try to refactor your code so others will understand it without a comment."
	private final static String ORPHAN_MESSAGE = "Loosely comments " + DEFAULT_MESSAGE
	private final static String JAVADOC_MESSAGE = "Javadoc over private or package private methods " + DEFAULT_MESSAGE

	private List<CommentSmell> smells = new ArrayList<>()
	private Path path

	MethodVisitor(Path path) {
		this.path = path
	}

	@Override
	void visit(MethodDeclaration n, Object arg) {
		if (n.comment != null) {
			def modifiers = n.modifiers
			if (ModifierSet.isPrivate(modifiers) || ModifierSet.hasPackageLevelAccess(modifiers)) {
				addCommentSmell(CommentSmell.PRIVATE, n.comment, JAVADOC_MESSAGE)
			}
		}

		for (comment in n.getAllContainedComments()) {
			addCommentSmell(CommentSmell.ORPHAN, comment, ORPHAN_MESSAGE)
		}

		super.visit(n, arg)
	}

	private void addCommentSmell(String type, Comment comment, String message) {
		def sloc = positions(comment)
		smells.add(new CommentSmell(type, comment.toString(), message, sloc,
				path.toAbsolutePath().normalize().toString()))
	}

	private static Tuple2<SourcePosition, SourcePosition> positions(Comment comment) {
		return new Tuple2<SourcePosition, SourcePosition>(
				new SourcePosition(Position.beginOf(comment).line, Position.beginOf(comment).column),
				new SourcePosition(Position.endOf(comment).line, Position.endOf(comment).column))
	}

	List<CommentSmell> getSmells() {
		return smells
	}

}
