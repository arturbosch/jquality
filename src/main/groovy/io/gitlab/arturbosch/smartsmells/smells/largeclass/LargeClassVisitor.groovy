package io.gitlab.arturbosch.smartsmells.smells.largeclass

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.util.JavaLoc

import java.nio.file.Files
import java.util.stream.Stream

/**
 * @author artur
 */
class LargeClassVisitor extends Visitor<LargeClass> {

	private int sizeThreshold

	LargeClassVisitor(int sizeThreshold) {
		this.sizeThreshold = sizeThreshold
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver resolver) {
		if (n.interface) return

		def sum
		try {
			sum = JavaLoc.analyze(Files.readAllLines(info.path), false, false)
		} catch (IOException ignored) {
			sum = calcSizeFromNode(n)
		}

		if (sum >= sizeThreshold)
			smells.add(new LargeClass(n.nameAsString, ClassHelper.createFullSignature(n),
					sum.toInteger(), sizeThreshold,
					SourcePath.of(path), SourceRange.fromNode(n), ElementTarget.CLASS))
	}

	private static int calcSizeFromNode(ClassOrInterfaceDeclaration n) {
		def commentsSize = Stream.of(n.getAllContainedComments())
				.flatMap { it.stream() }
				.mapToInt {
			def beginLine = it.begin.map { it.line }.orElse(0)
			def endLine = it.end.map { it.line }.orElse(0)
			(endLine - beginLine) + 1
		}.sum()
		def size = n.end.map { it.line }.orElse(0) - n.begin.map { it.line }.orElse(0) + 1
		return size - commentsSize
	}

}
