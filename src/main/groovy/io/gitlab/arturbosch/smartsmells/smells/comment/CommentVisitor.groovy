package io.gitlab.arturbosch.smartsmells.smells.comment

import com.github.javaparser.ast.AccessSpecifier
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.comments.Comment
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * Visits all method declaration of a compilation unit and examines them
 * for orphan comments or comments above private/package-private methods.
 *
 * @author artur
 */
class CommentVisitor extends Visitor<CommentSmell> {

	@Override
	void visit(MethodDeclaration n, Resolver arg) {
		def comment = n.comment.orElse(null)
		if (comment != null) {
			def modifiers = n.modifiers
			def specifier = Modifier.getAccessSpecifier(modifiers)
			if (specifier == AccessSpecifier.PRIVATE || specifier == AccessSpecifier.DEFAULT) {
				addCommentSmell(CommentSmell.Type.PRIVATE, n.declarationAsString, comment, ElementTarget.METHOD)
			}
		}

		n.getAllContainedComments().each {
			addCommentSmell(CommentSmell.Type.ORPHAN, "orphan comment", it, ElementTarget.NOT_SPECIFIED)
		}

		super.visit(n, arg)
	}

	private void addCommentSmell(CommentSmell.Type type, String name, Comment comment, ElementTarget elementTarget) {

		smells.add(new CommentSmell(type, name,
				hasTodoOrFixme(comment, "TODO"),
				hasTodoOrFixme(comment, "FIXME"),
				SourcePath.of(path), SourceRange.fromNode(comment), elementTarget))
	}

	private static boolean hasTodoOrFixme(Comment comment, String pattern) {
		comment.content.contains(pattern)
	}
}
