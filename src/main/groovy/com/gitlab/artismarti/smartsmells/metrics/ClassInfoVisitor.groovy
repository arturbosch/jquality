package com.gitlab.artismarti.smartsmells.metrics

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.common.helper.BadSmellHelper
import com.gitlab.artismarti.smartsmells.common.helper.MetricHelper

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
			smells.add(new ClassInfo(
					name: it.name,
					signature: BadSmellHelper.createClassSignature(it),
					path: path.toString(),
					wmc: MetricHelper.wmc(it),
					tcc: MetricHelper.tcc(it),
					atfd: MetricHelper.atfd(it),
					noa: MetricHelper.noa(it),
					nom: MetricHelper.nom(it),
					loc: MetricHelper.loc(path),
					sloc: MetricHelper.sloc(path),
			))
		}

	}
}
