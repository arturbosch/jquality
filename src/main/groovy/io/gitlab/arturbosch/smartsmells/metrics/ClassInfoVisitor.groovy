package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.CompositeMetricRaiser
import io.gitlab.arturbosch.smartsmells.common.visitor.InternalVisitor

/**
 * @author Artur Bosch
 */
class ClassInfoVisitor extends InternalVisitor {

	private final CompositeMetricRaiser metrics
	final Set<ClassInfo> classes = new HashSet<>()

	ClassInfoVisitor(final CompositeMetricRaiser compositeMetricRaiser) {
		metrics = compositeMetricRaiser
	}

	@Override
	void visit(CompilationUnit n, Resolver resolver) {
		metrics.init { it.each { it.setResolver(resolver) } }
		super.visit(n, resolver)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration it, Resolver arg) {
		def metrics = metrics.raise(it).collectEntries { [it.type, it] }
		def qualifiedType = info.getQualifiedTypeBySimpleName(it.nameAsString)
				.orElse(arg.resolveType(new ClassOrInterfaceType(it.nameAsString), info))
		def signature = ClassHelper.createFullSignature(it)
		classes.add(new ClassInfo(
				qualifiedType,
				signature,
				metrics,
				SourcePath.of(info),
				SourceRange.fromNode(it)))
		super.visit(it, arg)
	}
}
