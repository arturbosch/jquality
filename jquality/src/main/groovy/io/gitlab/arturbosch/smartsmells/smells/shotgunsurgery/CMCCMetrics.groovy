package io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.utils.Pair
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.TypeHelper
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.resolution.Resolver

import java.util.stream.Collectors

/**
 * Special version of Metrics.cc() and Metrics.cm() for performance reasons.
 *
 * @author Artur Bosch
 */
final class CMCCMetrics {
	private CMCCMetrics() {}

	static Pair<Integer, Integer> raise(ClassOrInterfaceDeclaration n, Resolver resolver) {
		int cm = 0
		int cc = 0
		TypeHelper.getQualifiedType(n).ifPresent { type ->

			def methods = NodeHelper.findMethods(n)
					.stream()
					.filter { ClassHelper.inClassScope(it, n.nameAsString) }
					.map { it.name }
					.collect(Collectors.toSet())

			for (CompilationInfo info : resolver.storage.getAllCompilationInfo()) {
				if (info.isWithinScope(type)) {
					cc += 1
					cm += info.unit.getChildNodesByType(MethodCallExpr.class)
							.stream()
							.filter { methods.contains(it.name) }
							.mapToInt { 1 }
							.sum()
				}
			}
		}
		return new Pair(cm, cc)
	}
}
