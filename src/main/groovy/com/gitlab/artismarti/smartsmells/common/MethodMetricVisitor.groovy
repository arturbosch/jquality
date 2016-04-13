package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.Statement
import com.gitlab.artismarti.smartsmells.domain.SourcePath
import com.gitlab.artismarti.smartsmells.domain.SourceRange
import com.gitlab.artismarti.smartsmells.longmethod.LongMethod

import java.nio.file.Path

/**
 * @author artur
 */
abstract class MethodMetricVisitor<T> extends Visitor<T> {

	int threshold

	MethodMetricVisitor(int threshold, Path path) {
		super(path)
		this.threshold = threshold
	}

	@Override
	void visit(MethodDeclaration node, Object arg) {
		Optional.ofNullable(node.body)
				.map({ it.stmts })
				.filter({ byThreshold(node, it) })
				.ifPresent({ addSmell(node, it) }
		)
	}

	protected LongMethod newLongMethod(MethodDeclaration n, List<Statement> it) {
		new LongMethod(
				n.declarationAsString, n.name, n.getDeclarationAsString(false, false, true),
				it.size(), threshold,
				SourceRange.of(n.getBeginLine(), n.getBeginColumn(), n.getEndLine(), n.getEndColumn()),
				SourcePath.of(path)
		)
	}

	protected abstract byThreshold(MethodDeclaration n, List<Statement> stmt)

	protected abstract addSmell(MethodDeclaration n, List<Statement> stmt)
}
