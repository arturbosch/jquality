package io.gitlab.arturbosch.smartsmells.smells.middleman

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.stmt.ReturnStmt
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.metrics.Metrics
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author artur
 */
class MiddleManVisitor extends Visitor<MiddleMan> {

	private threshold = MMT.all

	enum MMT {
		all, half, third
	}

	MiddleManVisitor(MMT threshold) {
		this.threshold = threshold
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver resolver) {
		if (n.interface) return // interfaces are no MM
		// although default methods are often used to delegate simple cases - #82
		if (ClassHelper.isEmptyBody(n)) return
		if (ClassHelper.hasNoMethods(n)) return

		def methods = NodeHelper.findMethods(n)

		def partition = methods.groupBy {
			hasBodySizeOne(it) &&
					hasComplexityOfOne(it) &&
					useSameParametersForMethodInvocation(it) &&
					hasNoFilteredAnnotations(it)
		}

		if (checkThreshold(partition)) {
			smells.add(new MiddleMan(n.nameAsString, ClassHelper.createFullSignature(n),
					SourcePath.of(info), SourceRange.fromNode(n), ElementTarget.CLASS))
		}

		super.visit(n, resolver)
	}

	private boolean checkThreshold(Map<Boolean, List<MethodDeclaration>> map) {

		def trueSize = map[true] ? map[true].size() : 0
		def falseSize = map[false] ? map[false].size() : 0
		def allSize = trueSize + falseSize

		def match = false
		switch (threshold) {
			case threshold.all:
				match |= (trueSize == allSize && allSize > 1)
				break
			case threshold.half:
				match |= (trueSize.toBigDecimal() >= (allSize / 2) && allSize > 3)
				break
			case threshold.third:
				match |= (trueSize.toBigDecimal() >= (allSize / 3) && allSize > 5)
				break
		}
		return match
	}

	private static boolean hasBodySizeOne(MethodDeclaration method) {
		return method.body
				.filter { it.statements.size() == 1 }
				.isPresent()
	}

	private static boolean useSameParametersForMethodInvocation(MethodDeclaration method) {
		return method.body.filter { it.statements.size() == 1 }
				.map { it.statements[0] }
				.filter { it instanceof ReturnStmt }
				.map { (it as ReturnStmt).expression }
				.filter { it.isPresent() }
				.map { it.get() }
				.filter { it instanceof MethodCallExpr }
				.map {
			def argsExpressions = (it as MethodCallExpr).arguments.collect { it.toString(Printer.NO_COMMENTS) }
			def argsMethod = method.parameters.collect { it.nameAsString }
			argsExpressions.containsAll(argsMethod)
		}.isPresent()
	}

	private static boolean hasComplexityOfOne(MethodDeclaration it) {
		Metrics.mcCabe(it) == 1
	}

	private static boolean hasNoFilteredAnnotations(MethodDeclaration method) {
		method.annotations.stream().noneMatch { it.nameAsString == "Bean" }
	}

}

