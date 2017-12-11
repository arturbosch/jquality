package io.gitlab.arturbosch.smartsmells.metrics

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.CallableDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.type.ClassOrInterfaceType
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.MethodHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.CompositeMetricRaiser
import io.gitlab.arturbosch.smartsmells.common.visitor.InternalVisitor
import io.gitlab.arturbosch.smartsmells.metrics.internal.FullstackMetrics
import io.gitlab.arturbosch.smartsmells.metrics.raisers.AMW
import io.gitlab.arturbosch.smartsmells.metrics.raisers.ATFD
import io.gitlab.arturbosch.smartsmells.metrics.raisers.CC_CM
import io.gitlab.arturbosch.smartsmells.metrics.raisers.CYCLO
import io.gitlab.arturbosch.smartsmells.metrics.raisers.CompositeMethodMetricRaiser
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MAXNESTING
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MLOC
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MethodMetricRaiser
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MetricPostRaiser
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MetricPreListener
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NAS
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NOAV
import io.gitlab.arturbosch.smartsmells.metrics.raisers.TCC
import io.gitlab.arturbosch.smartsmells.metrics.raisers.WMC

import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
@CompileStatic
class MetricDetector extends InternalVisitor {

	static final CompositeMetricRaiser metrics = FullstackMetrics.create()

	static final List<MetricPreListener> preRaisers =
			[new NAS(), new CC_CM(), new TCC(), new ATFD()].sort { it.priority() } as List<MetricPreListener>

	static final List<MetricPostRaiser> postRaisers =
			[new AMW(), new WMC()].sort { it.priority() } as List<MetricPostRaiser>

	@Override
	void visit(CompilationUnit n, Resolver arg) {
		FileInfo fileInfo = new FileInfo(SourcePath.of(info), SourceRange.fromNode(info.unit))
		info.setData(FileInfo.KEY, fileInfo)
		metrics.init { it.each { it.setResolver(arg) } }
		super.visit(n, arg)
	}

	@Override
	void visit(ClassOrInterfaceDeclaration aClass, Resolver arg) {
		def metrics = metrics.raise(aClass).collectEntries { [it.type, it] }
		def qualifiedType = info.getQualifiedTypeBySimpleName(aClass.nameAsString)
				.orElse(arg.resolveType(new ClassOrInterfaceType(aClass.nameAsString), info))
		def signature = ClassHelper.createFullSignature(aClass)
		def currentClazz = new ClassInfo(qualifiedType, signature,
				metrics, SourcePath.of(info), SourceRange.fromNode(aClass))
		statistics().addClass(currentClazz)
		new MethodInfoVisitor(currentClazz).visit(aClass, arg)
		preRaisers.each { it.raise(aClass, info, arg) }
		super.visit(aClass, arg)
		postRaisers.each { it.raise(aClass, info, arg) }
	}
}

@CompileStatic
class MethodInfoVisitor extends InternalVisitor {

	static final Set<MethodMetricRaiser> methodRaisers =
			[new CYCLO(), new MAXNESTING(), new NOAV()].toSet() as Set<MethodMetricRaiser>

	static final Set<CompositeMethodMetricRaiser> compositeMethodRaisers =
			[new MLOC()].toSet() as Set<CompositeMethodMetricRaiser>

	private ClassInfo current

	MethodInfoVisitor(ClassInfo current) {
		this.current = current
	}

	@Override
	void visit(ConstructorDeclaration n, Resolver arg) {
		if (ClassHelper.inClassScope(n, current.name)) {
			handleCallable(n, arg)
		}
	}

	@Override
	void visit(MethodDeclaration n, Resolver arg) {
		if (!MethodHelper.isAnonymousMethod(n) && ClassHelper.inClassScope(n, current.name)) {
			handleCallable(n, arg)
		}
	}

	private void handleCallable(CallableDeclaration n, Resolver arg) {
		List<Metric> metrics = methodRaisers.collect { it.raise(n, arg) }
		List<Metric> nestedMetrics = compositeMethodRaisers.stream()
				.flatMap { it.raise(n, arg).stream() }
				.collect(Collectors.toList())
		def info = MethodInfo.of(n, current, metrics + nestedMetrics)
		current.addMethodInfo(info)
	}
}
