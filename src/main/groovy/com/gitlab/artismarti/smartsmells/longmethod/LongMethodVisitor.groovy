package com.gitlab.artismarti.smartsmells.longmethod

import com.github.javaparser.ast.body.MethodDeclaration
import com.gitlab.artismarti.smartsmells.common.Visitor
/**
 * @author artur
 */
class LongMethodVisitor extends Visitor<LongMethod> {

	int threshold

	List<LongMethod> result

	LongMethodVisitor(int threshold) {
		this.threshold = threshold
	}

	@Override
	void visit(MethodDeclaration n, Object arg) {
		def size = Optional.ofNullable(n.body)
				.map({ it.stmts })
				.map({ it.size() })

		if (size >= threshold) {
			result.add(new LongMethod(n.name))
		}
	}
}
