package com.gitlab.artismarti.smartsmells.metrics

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.common.helper.BadSmellHelper
import com.gitlab.artismarti.smartsmells.common.source.SourcePath

import java.nio.file.Path

/**
 * @author artur
 */
class ClassInfoVisitor extends Visitor<ClassInfo> {

	ClassInfoVisitor(Path path) {
		super(path)
	}

	@Override
	void visit(CompilationUnit n, Object arg) {

		def classes = ASTHelper.getNodesByType(n, ClassOrInterfaceDeclaration.class)

		classes.each {

			def methods = ASTHelper.getNodesByType(it, MethodDeclaration.class)
			def methodCount = methods.isEmpty() ? 1 : methods.size()

			def apl = methods.stream().mapToInt { it.parameters.size() }.sum() / methodCount
			def aml = methods.stream().map { Optional.ofNullable(it.body) }.filter { it.isPresent() }.map { it.get() }
					.mapToInt { it.stmts.size() }.sum() / methodCount

			smells.add(new ClassInfo(
					name: it.name,
					signature: BadSmellHelper.createClassSignature(it),
					sourcePath: SourcePath.of(path),
					sourceRange: BadSmellHelper.createSourceRangeFromNode(it),
					wmc: Metrics.wmc(it),
					tcc: Metrics.tcc(it),
					atfd: Metrics.atfd(it),
					noa: Metrics.noa(it),
					nom: Metrics.nom(it),
					loc: Metrics.loc(it, path),
					sloc: Metrics.sloc(it, path),
					aml: aml,
					apl: apl,
					cc: -1/*Metrics.cc(it)*/,
					cm: -1/*Metrics.cm(it)*/
			))
		}

	}
}
