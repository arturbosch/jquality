package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.JavaParser
import com.github.javaparser.ParseException
import com.github.javaparser.ast.CompilationUnit
import com.gitlab.artismarti.smartsmells.util.Cache
import com.gitlab.artismarti.smartsmells.util.StreamCloser
import org.codehaus.groovy.runtime.IOGroovyMethods

import java.nio.file.Files
import java.nio.file.Path

/**
 * @author artur
 */
class CompilationTree {

	private static Cache<String, Path> qualifiedNameToPathCache = new Cache<String, Path>() {}
	private static Cache<Path, CompilationUnit> pathToCompilationUnitCache = new Cache<Path, CompilationUnit>() {}

	private static Path root

	private static Optional<CompilationUnit> getUnit(Path path) {
		return Optional.ofNullable(pathToCompilationUnitCache.verifyAndReturn(path))
	}

	private static Optional<CompilationUnit> compileFor(Path path) {
		return IOGroovyMethods.withCloseable(Files.newInputStream(path)) {
			try {
				def compilationUnit = JavaParser.parse(it)
				pathToCompilationUnitCache.putPair(path, compilationUnit)
				Optional.of(compilationUnit)
			} catch (ParseException ignored) {
				Optional.empty()
			}
		}
	}

	static Optional<Path> findReferencedType(QualifiedType qualifiedType) {

		def maybePath = Optional.ofNullable(qualifiedNameToPathCache.verifyAndReturn(qualifiedType.name))

		if (maybePath.isPresent()) {
			return maybePath
		} else {
			def search = qualifiedType.asStringPathToJavaFile()

			def walker = Files.walk(root)
			def pathToQualifier = walker
					.filter { it.endsWith(search) }
					.findFirst()
					.map { it.toAbsolutePath().normalize() }
			StreamCloser.quietly(walker)

			pathToQualifier.ifPresent { qualifiedNameToPathCache.putPair(qualifiedType.name, it) }

			return pathToQualifier
		}

	}

	static Optional<CompilationUnit> getCompilationUnit(Path path) {
		def maybeUnit = getUnit(path)
		def unit
		if (maybeUnit.isPresent()) {
			unit = maybeUnit
		} else {
			unit = compileFor(path)
		}
		unit
	}

	static def registerRoot(Path path) {
		root = path
	}

	static def reset() {
		qualifiedNameToPathCache.reset()
		pathToCompilationUnitCache.reset()
	}
}
