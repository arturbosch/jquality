package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.CompositeMetricRaiser
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
class ClassInfoVisitor extends Visitor<ClassInfo> {

	private CompositeMetricRaiser metrics

	ClassInfoVisitor(CompositeMetricRaiser compositeMetricRaiser) {
		metrics = compositeMetricRaiser
	}

	@Override
	void visit(CompilationUnit n, Resolver resolver) {
		metrics.init { it.each { it.setResolver(resolver) } }
		def classes = n.getNodesByType(ClassOrInterfaceDeclaration.class)

		classes.each {

			smells.add(new ClassInfo(
					name: it.name,
					signature: ClassHelper.createFullSignature(it),
					metrics: metrics.raise(it),
					sourcePath: SourcePath.of(info),
					sourceRange: SourceRange.fromNode(it),
					elementTarget: ElementTarget.CLASS
			))
		}

	}
}
