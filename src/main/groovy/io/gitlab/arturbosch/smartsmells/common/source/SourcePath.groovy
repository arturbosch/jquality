package io.gitlab.arturbosch.smartsmells.common.source

import groovy.transform.Immutable
import groovy.transform.ToString

import java.nio.file.Path

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false, includeNames = false)
class SourcePath {

	String path

	static def of(Path path) {
		new SourcePath(path.toAbsolutePath().normalize().toString())
	}

	@Override
	public String toString() {
		return path
	}
}
