package com.gitlab.artismarti.smartsmells.cycle

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.gitlab.artismarti.cache.Cache
import org.codehaus.groovy.runtime.IOGroovyMethods

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author artur
 */
class CompilationTree {

	private static Cache<Path, CompilationUnit> cache = new Cache<Path, CompilationUnit>() {}

	static Optional<CompilationUnit> getUnit(Path path) {
		return Optional.ofNullable(cache.verifyAndReturn(path))
	}

	static CompilationUnit compileFor(Path path) {
		def fis = Files.newInputStream(path)
		def unit = IOGroovyMethods.withCloseable(fis) {
			JavaParser.parse(fis)
		}
		cache.putPair(path, unit)
		return unit
	}
}
