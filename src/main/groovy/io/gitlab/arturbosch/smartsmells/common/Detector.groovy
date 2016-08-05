package io.gitlab.arturbosch.smartsmells.common

import com.github.javaparser.ast.CompilationUnit
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.util.StreamCloser

import java.nio.file.Files
import java.nio.file.Path
import java.util.function.BinaryOperator
import java.util.stream.Collectors

/**
 * @author artur
 */
abstract class Detector<T extends Smelly> {

	protected Smell type = getType()
	private Deque<T> smells = new ArrayDeque<>(100)

	/**
	 * Binary operator combines two sets into one.
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
	 * Attention!!! Only use this method if running this detector in single mode.
	 *
	 * Walks from the given base path down all directories and analyzes java source files.
	 *
	 * @param startPath start path of analyzing project
	 * @return set of smells
	 */
	Set<T> run(Path startPath) {
		def walker = Files.walk(startPath)
		def result = walker
				.filter({ it.fileName.toString().endsWith("java") })
				.map({
			def maybeUnit = CompilationTree.getCompilationUnit(it)
			maybeUnit.isPresent() ? execute(maybeUnit.get(), it) : Collections.emptySet()
		}).collect(Collectors.reducing(new HashSet(), op))
		StreamCloser.quietly(walker)
		return result
	}

	/**
	 * This method is called from detector facade which handles all paths.
	 *
	 * @param path current file path
	 * @return smells of current analyzed file
	 */
	Set<T> execute(CompilationUnit unit, Path path) {
		def visitor = getVisitor(path)
		visitor.visit(unit, null)
		def newSmells = visitor.smells
		smells.addAll(newSmells)
		return newSmells
	}

	/**
	 * All subclasses must specify a visitor.
	 *
	 * @param path of current file which the visitor needs to save
	 * @return visitor for specific smell
	 */
	protected abstract Visitor getVisitor(Path path)

	abstract Smell getType()

	Deque<T> getSmells() {
		return smells
	}
}
