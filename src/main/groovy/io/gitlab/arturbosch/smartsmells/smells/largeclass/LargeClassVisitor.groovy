package io.gitlab.arturbosch.smartsmells.smells.largeclass

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.util.StreamCloser

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
			def walker = Files.lines(path)
			sum = walker
					.filter { isNotEmpty(it) }
					.filter { hasNotSizeOneWhichIndicatesBraces(it) }
					.filter { isNoPackageDeclaration(it) }
					.filter { isNoImportStatement(it) }
					.filter { isNoComment(it) }
					.count()
			StreamCloser.quietly(walker)
		} catch (UncheckedIOException | IOException ignored) {
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

	private static boolean isNoComment(String it) {
		!(it.trim().startsWith("/*") || it.trim().startsWith("*") || it.trim().startsWith("*/") || it.trim().startsWith("//"))
	}

	private static boolean isNoImportStatement(String it) {
		!it.trim().startsWith("import")
	}

	private static boolean isNoPackageDeclaration(String it) {
		!it.trim().startsWith("package")
	}

	private static boolean hasNotSizeOneWhichIndicatesBraces(String it) {
		it.trim().size() != 1
	}

	private static boolean isNotEmpty(String it) {
		!it.trim().empty
	}
}
