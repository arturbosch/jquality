package io.gitlab.arturbosch.smartsmells.smells.largeclass

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.util.JavaLoc

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

/**
 * @author artur
 */
class LargeClassVisitor extends Visitor<LargeClass> {

	private int sizeThreshold

	LargeClassVisitor(Path path, int sizeThreshold) {
		super(path)
		this.sizeThreshold = sizeThreshold
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {
		if (n.interface) return

		def sum
		try {
			sum = JavaLoc.analyze(Files.readAllLines(path), false, false)
		} catch (IOException ignored) {
			sum = calcSizeFromNode(n)
		}

		if (sum >= sizeThreshold)
			smells.add(new LargeClass(n.name, ClassHelper.createFullSignature(n),
					sum.toInteger(), sizeThreshold,
					SourcePath.of(path), SourceRange.fromNode(n)))
	}

	private static int calcSizeFromNode(ClassOrInterfaceDeclaration n) {
		def commentsSize = Stream.of(n.getAllContainedComments())
				.flatMap { it.stream() }
				.mapToInt { (it.end.line - it.begin.line) + 1 }
				.sum()
		def size = n.end.line - n.begin.line + 1
		return size - commentsSize
	}

}
