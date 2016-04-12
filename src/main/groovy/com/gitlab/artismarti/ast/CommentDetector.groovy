package com.gitlab.artismarti.ast

import com.github.javaparser.JavaParser
import org.codehaus.groovy.runtime.IOGroovyMethods

import java.nio.file.Files
import java.nio.file.Path
import java.util.function.BinaryOperator
import java.util.stream.Collectors

/**
 * Detector finds orphan comments within methods or not very useful comments over private/package-private methods.
 * Comments are considered as a smell because they shade poor code which should be rewritten instead.
 *
 * @author artur
 */
class CommentDetector {

	/**
	 * Binary operator combines two lists into one.
	 */
	static def op = new BinaryOperator<List<CommentSmell>>() {
		@Override
		List<CommentSmell> apply(List<CommentSmell> commentSmell, List<CommentSmell> commentSmell2) {
			List<CommentSmell> list = new ArrayList<>(commentSmell)
			list.addAll(commentSmell2)
			return list
		}
	}

	/**
	 * Walks from the given base path down all dirs and analyzes java source files.
	 * @param startPath project path
	 * @return list of comment smells
	 */
	static List<CommentSmell> run(Path startPath) {
		return Files.walk(startPath)
				.filter({ it.fileName.toString().endsWith("java") })
				.map({ execute(it) })
				.collect(Collectors.reducing(new ArrayList(), op))

	}

	private static List<CommentSmell> execute(Path path) {
		def fis = Files.newInputStream(path)
		def visitor = new MethodVisitor(path)
		IOGroovyMethods.withCloseable(fis) {
			def unit = JavaParser.parse(fis)
			visitor.visit(unit, null)
		}
		return visitor.smells
	}
}
