package com.gitlab.artismarti.smartsmells.longmethod

import com.github.javaparser.ast.body.MethodDeclaration
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
	void visit(MethodDeclaration n, Object arg) {
		Optional.ofNullable(n.body)
				.map({ it.stmts })
				.filter({ it.size() > threshold })
				.ifPresent({
			smells.add(new LongMethod(n.name))
		})
	}
}
