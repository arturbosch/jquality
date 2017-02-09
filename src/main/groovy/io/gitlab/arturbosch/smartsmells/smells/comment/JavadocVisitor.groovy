package io.gitlab.arturbosch.smartsmells.smells.comment

import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.EnumDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.comments.JavadocComment
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc
import com.github.javaparser.ast.nodeTypes.NodeWithModifiers
import com.github.javaparser.ast.type.VoidType
import com.github.javaparser.javadoc.Javadoc
import com.github.javaparser.javadoc.JavadocBlockTag
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.EnumHelper
import io.gitlab.arturbosch.jpal.ast.MethodHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
@CompileStatic
class JavadocVisitor extends Visitor<CommentSmell> {

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver arg) {
		if (isPublic(n)) {
			checkForJavadoc(n, ClassHelper.createFullSignature(n))
		}
		super.visit(n, arg)
	}

	@Override
	void visit(EnumDeclaration n, Resolver arg) {
		if (isPublic(n)) {
			checkForJavadoc(n, EnumHelper.createFullSignature(n))
		}
		super.visit(n, arg)
	}

	private static boolean isPublic(NodeWithModifiers node) {
		return node.modifiers.contains(Modifier.PUBLIC)
	}

	private void checkForJavadoc(NodeWithJavadoc node, String forNode) {
		def comment = node.getComment().orElse(null)
		if (!comment instanceof JavadocComment || !node.javadoc.isPresent()) {
			smells.add(new CommentSmell(CommentSmell.Type.MISSING_JAVADOC,
					forNode, false, false, SourcePath.of(relativePath),
					SourceRange.fromNode(node as Node), ElementTarget.CLASS))
		}
	}

	@Override
	void visit(MethodDeclaration n, Resolver arg) {
		if (isPublic(n) && !MethodHelper.isGetterOrSetter(n)) {
			def comment = n.getComment().orElse(null)
			if (comment && comment instanceof JavadocComment) {
				def javadoc = n.javadoc.orElse(null)
				def javadocComment = n.javadocComment.orElse(null)
				if (javadoc && javadocComment) {
					def fixme = javadocComment.content.contains("FIXME")
					def todo = javadocComment.content.contains("TODO")
					checkForParameterTags(javadoc, n, javadocComment, fixme, todo)
					checkForReturnTag(n, javadoc, javadocComment)
				} else {
					smells.add(new CommentSmell(CommentSmell.Type.MISSING_JAVADOC, n.declarationAsString,
							false, false, SourcePath.of(relativePath), SourceRange.fromNode(n), ElementTarget.METHOD))
				}
			}
		}
		super.visit(n, arg)
	}

	private void checkForParameterTags(Javadoc javadoc, MethodDeclaration n,
									   JavadocComment javadocComment, boolean fixme, boolean todo) {
		Collection<JavadocBlockTag> tags = javadoc.blockTags.stream()
				.filter { it.type == JavadocBlockTag.Type.PARAM }
				.filter { it.name.isPresent() }
				.collect()
		Map<Boolean, List<String>> parameters = n.parameters.stream()
				.map { it.nameAsString }
				.collect()
				.groupBy { param ->
			def tag = tags.find { (it.name.get() == param) }
			tag && !tag.content.empty
		}

		List<String> paramNames = parameters[false]
		if (paramNames) {
			paramNames.each { String it ->
				missingParameterName(javadocComment, it, fixme, todo)
			}
		}
	}

	private void missingParameterName(JavadocComment javadoc, String parameter, boolean fixme, boolean todo) {
		smells.add(new CommentSmell(CommentSmell.Type.MISSING_PARAMETER, parameter,
				todo, fixme, SourcePath.of(relativePath), SourceRange.fromNode(javadoc), ElementTarget.METHOD))
	}

	private void checkForReturnTag(MethodDeclaration n, Javadoc javadoc, JavadocComment javadocComment) {
		if (!(n.type instanceof VoidType)) {
			def returnTag = javadoc.blockTags.find { it.type == JavadocBlockTag.Type.RETURN }
			if (!returnTag || returnTag.content.empty)
				smells.add(new CommentSmell(CommentSmell.Type.MISSING_RETURN,
						"${n.getType().toString(Printer.NO_COMMENTS)}",
						false, false, SourcePath.of(relativePath), SourceRange.fromNode(javadocComment), ElementTarget.METHOD))
		}
	}
}
