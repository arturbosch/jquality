package com.gitlab.artismarti.smartsmells.common

import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.util.StreamCloser

import java.nio.file.Files
import java.nio.file.Path
import java.util.function.BinaryOperator
import java.util.logging.Logger
import java.util.stream.Collectors

/**
 * @author artur
 */
abstract class Detector<T extends Smelly> {

	protected Logger logger = Logger.getLogger(getClass().simpleName)

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
				.map({ execute(it) })
				.collect(Collectors.reducing(new HashSet(), op))
		StreamCloser.quietly(walker)
		return result
	}

	/**
	 * This method is called from detector facade which handles all paths.
	 *
	 * @param path current file path
	 * @return smells of current analyzed file
	 */
	Set<T> execute(Path path) {
		def visitor = getVisitor(path)
		def maybeUnit = CompilationTree.getCompilationUnit(path)
		def newSmells = Collections.emptySet()
		if (maybeUnit.isPresent()) {
			visitor.visit(maybeUnit.get(), null)
			newSmells = visitor.smells
			smells.addAll(newSmells)
		} else {
			logger.warning("Could not create compilation unit from: $path due to syntax errors.")
		}
		return newSmells
	}

	/**
	 * All subclasses must specify visitor.
	 *
	 * @param path of current file
	 * @return visitor for specific smell
	 */
	protected abstract Visitor getVisitor(Path path)

	abstract Smell getType()

	Deque<T> getSmells() {
		return smells
	}
}
