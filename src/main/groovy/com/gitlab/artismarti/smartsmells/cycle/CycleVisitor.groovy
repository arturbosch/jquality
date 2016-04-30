package com.gitlab.artismarti.smartsmells.cycle

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.gitlab.artismarti.smartsmells.common.NodeHelper
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class CycleVisitor extends Visitor<Cycle> {

	private PackageImportHelper packageImportHelper

	CycleVisitor(Path path) {
		super(path)
	}

	@Override
	void visit(CompilationUnit n, Object arg) {
		packageImportHelper = new PackageImportHelper(n.package, n.imports)
		super.visit(n, arg)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {

		def fields = NodeHelper.findFields(n)

		fields.stream()
				.map { packageImportHelper.getQualifiedType(it.type) }
				.filter { it.isReference() }
				.each {
			println it
		}

		super.visit(n, arg)
	}
}
