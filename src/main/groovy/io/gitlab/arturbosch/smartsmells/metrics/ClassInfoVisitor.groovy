package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.CallableDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.CompositeMetricRaiser
import io.gitlab.arturbosch.smartsmells.common.visitor.InternalVisitor
import io.gitlab.arturbosch.smartsmells.metrics.raisers.CYCLO
import io.gitlab.arturbosch.smartsmells.metrics.raisers.CompositeMethodMetricRaiser
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MAXNESTING
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MLOC
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MethodMetricRaiser
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NOAV

import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
@CompileStatic
class ClassInfoVisitor extends InternalVisitor {

	private static final Set<MethodMetricRaiser> methodRaisers =
			[new CYCLO(), new MAXNESTING(), new NOAV()].toSet() as Set<MethodMetricRaiser>

	private static final Set<CompositeMethodMetricRaiser> compositeMethodRaisers =
			[new MLOC()].toSet() as Set<CompositeMethodMetricRaiser>

	private final CompositeMetricRaiser metrics
	final Set<ClassInfo> classes = new HashSet<>()

	private ClassInfo currentClazz = ClassInfo.NOP

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
		currentClazz = new ClassInfo(
				qualifiedType,
				signature,
				metrics,
				SourcePath.of(info),
				SourceRange.fromNode(it))
		classes.add(currentClazz)
		super.visit(it, arg)
	}

	@Override
	void visit(ConstructorDeclaration n, Resolver arg) {
		handleCallable(n, arg)
	}

	@Override
	void visit(MethodDeclaration n, Resolver arg) {
		handleCallable(n, arg)
	}

	private void handleCallable(CallableDeclaration n, Resolver arg) {
		List<Metric> metrics = methodRaisers.collect { it.raise(n, arg) }
		List<Metric> nestedMetrics = compositeMethodRaisers.stream()
				.flatMap { it.raise(n, arg).stream() }
				.collect(Collectors.toList())
		Map<String, Metric> allMetrics = (metrics + nestedMetrics)
				.collectEntries { [it.type, it] }
		def declarationString = n.declarationAsString
		def info = new MethodInfo(n.nameAsString, declarationString, declarationString,
				allMetrics, currentClazz.sourcePath, SourceRange.fromNode(n))
		currentClazz.addMethodInfo(info)
	}
}
