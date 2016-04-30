package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.JavaParser
import org.codehaus.groovy.runtime.IOGroovyMethods

import java.nio.file.Files
import java.nio.file.Path
import java.util.function.BinaryOperator
import java.util.stream.Collectors

/**
 * @author artur
 */
abstract class Detector<T> {

	protected Path startPath
	Deque<T> smells = new ArrayDeque<>(100)

	/**
	 * Binary operator combines two lists into one.
	 */
	static def op = new BinaryOperator<Set<T>>() {
		@Override
		Set<T> apply(Set<T> l1, Set<T> l2) {
			Set<T> list = new HashSet<>(l1)
			list.addAll(l2)
			return list
		}
	}

	/**
	 * Walks from the given base path down all dirs and analyzes java source files.
	 * @param startPath project path
	 * @return list of comment smells
	 */
	Set<T> run(Path startPath) {
		this.startPath = startPath
		return Files.walk(startPath)
				.filter({ it.fileName.toString().endsWith("java") })
				.map({ execute(it) })
				.collect(Collectors.reducing(new HashSet(), op))
	}

	Set<T> execute(Path path) {
		def fis = Files.newInputStream(path)
		def visitor = getVisitor(path)
		visitor.startPath = startPath
		IOGroovyMethods.withCloseable(fis) {
			def unit = JavaParser.parse(fis)
			visitor.visit(unit, null)
		}
		smells.addAll(visitor.smells)
		return visitor.smells
	}

	protected abstract Visitor getVisitor(Path path)

}
