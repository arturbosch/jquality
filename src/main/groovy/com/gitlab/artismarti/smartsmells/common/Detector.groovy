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

	/**
	 * Binary operator combines two lists into one.
	 */
	static def op = new BinaryOperator<List<T>>() {
		@Override
		List<T> apply(List<T> l1, List<T> l2) {
			List<T> list = new ArrayList<>(l1)
			list.addAll(l2)
			return list
		}
	}

	/**
	 * Walks from the given base path down all dirs and analyzes java source files.
	 * @param startPath project path
	 * @return list of comment smells
	 */
	List<T> run(Path startPath) {
		return Files.walk(startPath)
				.filter({ it.fileName.toString().endsWith("java") })
				.map({ execute(it) })
				.collect(Collectors.reducing(new ArrayList(), op))
	}

	protected List<T> execute(Path path) {
		def fis = Files.newInputStream(path)
		def visitor = getVisitor(path)
		IOGroovyMethods.withCloseable(fis) {
			def unit = JavaParser.parse(fis)
			visitor.visit(unit, null)
		}
		return visitor.smells
	}

	protected abstract Visitor getVisitor(Path path)

}
