package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ast.CompilationUnit

import java.nio.file.Path

/**
 * @author artur
 */
class CompilationInfo {

	QualifiedType qualifiedType
	CompilationUnit unit
	Path path

	private CompilationInfo(QualifiedType qualifiedType, CompilationUnit unit, Path path) {
		this.qualifiedType = qualifiedType
		this.unit = unit
		this.path = path
	}

	static CompilationInfo of(QualifiedType qualifiedType, CompilationUnit unit, Path path) {
		new CompilationInfo(qualifiedType, unit, path)
	}
}
