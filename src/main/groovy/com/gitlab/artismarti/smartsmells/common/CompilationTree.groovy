package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ASTHelper
import com.github.javaparser.JavaParser
import com.github.javaparser.ParseException
import com.github.javaparser.TokenMgrError
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.gitlab.artismarti.smartsmells.util.Cache
import com.gitlab.artismarti.smartsmells.util.StreamCloser
import org.codehaus.groovy.runtime.IOGroovyMethods

import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer
import java.util.stream.Stream
/**
 * @author artur
 */
class CompilationTree {

	private static Cache<String, Path> qualifiedNameToPathCache =
			new Cache<String, Path>() {}
	private static Cache<Path, CompilationUnit> pathToCompilationUnitCache =
			new Cache<Path, CompilationUnit>() {}

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
			} catch (TokenMgrError ignored) {
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

			def walker = getJavaFilteredFileStream()
			def pathToQualifier = walker
					.filter { it.endsWith(search) }
					.findFirst()
					.map { it.toAbsolutePath().normalize() }
			StreamCloser.quietly(walker)

			pathToQualifier.ifPresent { qualifiedNameToPathCache.putPair(qualifiedType.name, it) }

			return pathToQualifier
		}

	}

	static int findReferencesFor(QualifiedType qualifiedType) {
		int references = 0
		findReferencesFor(qualifiedType, { references++ })
		return references
	}

	static int countMethodInvocations(QualifiedType qualifiedType, Collection<String> methods) {
		int calls = 0
		findReferencesFor(qualifiedType, {
			calls = ASTHelper.getNodesByType(it, MethodCallExpr.class)
					.stream()
					.filter { methods.contains(it.name) }
					.mapToInt { 1 }
					.sum()
		})
		return calls
	}

	static void findReferencesFor(QualifiedType qualifiedType, Consumer<CompilationUnit> code) {
		def walker = getJavaFilteredFileStream()
		walker.forEach {
			getCompilationUnit(it)
					.ifPresent {

				def imports = it.imports

				def maybeImport = imports.stream()
						.filter { it.name.toStringWithoutComments() == qualifiedType.name }
						.findFirst()

				if (maybeImport.isPresent()) {
					code.accept(it)
				} else if (searchForTypeWithinUnit(it, qualifiedType)) {
					code.accept(it)
				}

			}

		}
		StreamCloser.quietly(walker)
	}

	private static Stream<Path> getJavaFilteredFileStream() {
		Files.walk(root).filter { it.toString().endsWith(".java") }
	}

	static boolean searchForTypeWithinUnit(CompilationUnit unit, QualifiedType qualifiedType) {
		def shortName = qualifiedType.shortName()
		def types = ASTHelper.getNodesByType(unit, ClassOrInterfaceType.class)
		return types.any { it.name == shortName }
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
