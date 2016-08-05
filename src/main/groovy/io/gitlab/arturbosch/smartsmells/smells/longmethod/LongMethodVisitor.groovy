package io.gitlab.arturbosch.smartsmells.smells.longmethod

import com.github.javaparser.ast.body.BodyDeclaration
import io.gitlab.arturbosch.smartsmells.common.visitor.MethodMetricVisitor
import io.gitlab.arturbosch.smartsmells.util.JavaLoc

import java.nio.file.Path

/**
 * @author artur
 */
class LongMethodVisitor extends MethodMetricVisitor<LongMethod> {

	int size

	LongMethodVisitor(int threshold, Path path) {
		super(threshold, path)
	}

	@Override
	protected byThreshold(BodyDeclaration n) {
		size = JavaLoc.analyze(n.toString().split("\\n").toList(), false, false)
		return size > threshold
	}

	protected def addSmell(BodyDeclaration n) {
		smells.add(newLongMethod(n, size))
	}
}
