package io.gitlab.arturbosch.smartsmells.metrics

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
import io.gitlab.arturbosch.smartsmells.common.visitor.InternalVisitor
import io.gitlab.arturbosch.smartsmells.metrics.raisers.AMW
import io.gitlab.arturbosch.smartsmells.metrics.raisers.ATFD
import io.gitlab.arturbosch.smartsmells.metrics.raisers.CC_CM
import io.gitlab.arturbosch.smartsmells.metrics.raisers.CYCLO
import io.gitlab.arturbosch.smartsmells.metrics.raisers.LOC
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MAXNESTING
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MLOC
import io.gitlab.arturbosch.smartsmells.metrics.raisers.MethodMetricListener
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NAS
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NOA
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NOAV
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NOM
import io.gitlab.arturbosch.smartsmells.metrics.raisers.NOP
import io.gitlab.arturbosch.smartsmells.metrics.raisers.PostClassListener
import io.gitlab.arturbosch.smartsmells.metrics.raisers.PreClassListener
import io.gitlab.arturbosch.smartsmells.metrics.raisers.TCC
import io.gitlab.arturbosch.smartsmells.metrics.raisers.WMC

/**
 * @author Artur Bosch
 */
@CompileStatic
class MetricDetector extends InternalVisitor {

	static final List<PreClassListener> preRaisers =
			[new CC_CM(), new TCC(), new ATFD(), new LOC(), new NOM(), new NOA()]
					.sort { it.priority() } as List<PreClassListener>

	static final List<PostClassListener> postRaisers =
			[new AMW(), new WMC(), new NAS()]
					.sort { it.priority() } as List<PostClassListener>

	@Override
	void visit(ClassOrInterfaceDeclaration aClass, Resolver arg) {
		def qualifiedType = info.getQualifiedTypeBySimpleName(aClass.nameAsString)
				.orElse(arg.resolveType(new ClassOrInterfaceType(aClass.nameAsString), info))
		def signature = ClassHelper.createFullSignature(aClass)
		def currentClazz = new ClassInfo(qualifiedType, signature, SourcePath.of(info), SourceRange.fromNode(aClass))
		statistics().addClass(currentClazz)
		new MethodInfoVisitor(currentClazz).visit(aClass, arg)
		preRaisers.each { it.raise(aClass, info, arg) }
		super.visit(aClass, arg)
		postRaisers.each { it.raise(aClass, info, arg) }
	}
}

@CompileStatic
class MethodInfoVisitor extends InternalVisitor {

	static final List<MethodMetricListener> listeners =
			[new MLOC(), new NOP(), new CYCLO(), new MAXNESTING(), new NOAV()]
					.sort { it.priority() } as List<MethodMetricListener>

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
		def methodInfo = MethodInfo.of(n, current)
		current.addMethodInfo(methodInfo)
		listeners.each { it.raise(n, methodInfo, arg) }
	}
}
