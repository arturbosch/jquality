package com.gitlab.artismarti.smartsmells.longmethod

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.Statement
import com.gitlab.artismarti.smartsmells.common.SourceRange
import com.gitlab.artismarti.smartsmells.common.Visitor
/**
 * @author artur
 */
class LongMethodVisitor extends Visitor<LongMethod> {

	int threshold

	LongMethodVisitor(int threshold) {
		this.threshold = threshold
	}

	@Override
	void visit(MethodDeclaration node, Object arg) {
		Optional.ofNullable(node.body)
				.map({ it.stmts })
				.filter({ it.size() > threshold })
				.ifPresent({
			smells.add(newLongMethod(node, it))
		})
	}

	private LongMethod newLongMethod(MethodDeclaration n, List<Statement> it) {
		new LongMethod(n.declarationAsString, n.name, n.getDeclarationAsString(false, false, true),
				it.size(), threshold,
				SourceRange.of(n.getBeginLine(), n.getBeginColumn(), n.getEndLine(), n.getEndColumn()))
	}
}
