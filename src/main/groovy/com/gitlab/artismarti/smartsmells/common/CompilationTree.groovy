package com.gitlab.artismarti.smartsmells.common

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

	private static Cache<String, Path> qualifiedNameToPathCache = new Cache<String, Path>() {}
	private static Cache<Path, CompilationUnit> cache = new Cache<Path, CompilationUnit>() {}

	private static Path root

	private static Optional<CompilationUnit> getUnit(Path path) {
		return Optional.ofNullable(cache.verifyAndReturn(path))
	}

	private static CompilationUnit compileFor(Path path) {
		def unit = IOGroovyMethods.withCloseable(Files.newInputStream(path)) {
			JavaParser.parse(it)
		}
		cache.putPair(path, unit)
		return unit
	}

	static def registerRoot(Path path) {
		root = path
	}

	static Optional<Path> findReferencedType(QualifiedType qualifiedType) {

		def maybePath = Optional.ofNullable(qualifiedNameToPathCache.verifyAndReturn(qualifiedType.name))

		if (maybePath.isPresent()) {
			return maybePath
		} else {
			def search = "${qualifiedType.name.replaceAll("\\.", "/")}.java"

			def pathToQualifier = Files.walk(root)
					.filter { it.endsWith(search) }
					.findFirst()
					.map { it.toAbsolutePath().normalize() }

			pathToQualifier.ifPresent { qualifiedNameToPathCache.putPair(qualifiedType.name, it) }

			return pathToQualifier
		}

	}

	static CompilationUnit getCompilationUnit(Path path) {
		def maybeUnit = getUnit(path)
		def unit
		if (maybeUnit.isPresent()) {
			unit = maybeUnit.get()
		} else {
			unit = compileFor(path)
		}
		unit
	}
}
