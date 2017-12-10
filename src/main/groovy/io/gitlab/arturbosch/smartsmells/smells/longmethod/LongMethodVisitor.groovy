package io.gitlab.arturbosch.smartsmells.smells.longmethod

import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.CallableDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.smartsmells.common.visitor.MethodMetricVisitor
import io.gitlab.arturbosch.smartsmells.util.JavaLoc

/**
 * @author Artur Bosch
 */
@CompileStatic
class LongMethodVisitor extends MethodMetricVisitor<LongMethod> {

	int size

	LongMethodVisitor(int threshold) {
		super(threshold)
	}

	static int bodyLength(BodyDeclaration n) {
		JavaLoc.analyze(n.toString().split("\\n").toList(), false, false)
	}

	@Override
	protected byThreshold(CallableDeclaration n) {
		size = bodyLength(n)
		return size > threshold
	}

	protected addSmell(CallableDeclaration n) {
		smells.add(newLongMethod(n, size))
	}
}
