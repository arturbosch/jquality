package io.gitlab.arturbosch.jpal.ast.source

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.core.CompilationInfo

import java.nio.file.Path

/**
 * Represents a source path.
 *
 * @author artur
 */
@Immutable
@ToString(includePackage = false, includeNames = false)
@CompileStatic
class SourcePath {

	String path
	String relativePath

	static SourcePath of(Path path, Path relativePath) {
		return new SourcePath(path.toString(), relativePath.toString())
	}

	static SourcePath of(CompilationInfo info) {
		return new SourcePath(info.path.toString(), info.relativePath.toString())
	}

	@Override
	String toString() {
		return path
	}
}
