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

	private boolean skipCC_CM

	ClassInfoVisitor(Path path, boolean skipCC_CM) {
		super(path)
		this.skipCC_CM = skipCC_CM
	}

	@Override
	void visit(CompilationUnit n, Object arg) {

		def classes = ASTHelper.getNodesByType(n, ClassOrInterfaceDeclaration.class)

		classes.each {

			def methods = ASTHelper.getNodesByType(it, MethodDeclaration.class)
			int methodCount = methods.isEmpty() ? 1 : methods.size()


			def methodSizes = methods.stream().map { Optional.ofNullable(it.body) }.filter { it.isPresent() }
					.map { it.get() }.mapToInt() { it.stmts.size() }
			def amlSum = methodSizes.sum()
			def aml = amlSum / methodCount
			def sml = Math.sqrt(methods.stream().map { Optional.ofNullable(it.body) }.filter { it.isPresent() }
					.map { it.get() }.mapToInt { it.stmts.size() }
					.mapToDouble { Math.pow(it - amlSum, 2) }.sum() / methodCount)

			def aplSum = methods.stream().mapToInt { it.parameters.size() }.sum()
			def apl = aplSum / methodCount
			def spl = Math.sqrt(methods.stream().mapToInt { it.parameters.size() }
					.mapToDouble { Math.pow(it - aplSum, 2) }.sum() / methodCount)

			def cc = -1
			def cm = -1
			if(!skipCC_CM) {
				cc = Metrics.cc(it)
				cm = Metrics.cm(it)
			}

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
					mlm: aml,
					plm: apl,
					mld: sml,
					pld: spl,
					cc: cc,
					cm: cm
			))
		}

	}
}
