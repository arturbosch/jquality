package com.gitlab.artismarti.smartsmells.smells.middleman

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.stmt.ReturnStmt
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.common.helper.BadSmellHelper
import com.gitlab.artismarti.smartsmells.common.helper.MethodHelper
import com.gitlab.artismarti.smartsmells.common.helper.NodeHelper
import com.gitlab.artismarti.smartsmells.common.helper.TypeHelper
import com.gitlab.artismarti.smartsmells.common.source.SourcePath

import java.nio.file.Path
/**
 * @author artur
 */
class MiddleManVisitor extends Visitor<MiddleMan> {

	private threshold = MMT.all

	enum MMT {
		all, half, third
	}

	MiddleManVisitor(Path path, MMT threshold) {
		super(path)
		this.threshold = threshold
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {
		if (TypeHelper.isEmptyBody(n)) return
		if (TypeHelper.hasNoMethods(n)) return

		def methods = NodeHelper.findMethods(n)

		def partition = methods.groupBy {
			hasBodySizeOne(it) &&
					hasComplexityOfOne(it) &&
					useSameParametersForMethodInvocation(it) &&
					hasNoFilteredAnnotations(it)
		}

		if (checkThreshold(partition)) {
			smells.add(new MiddleMan(n.name, BadSmellHelper.createClassSignature(n),
					SourcePath.of(path), BadSmellHelper.createSourceRangeFromNode(n)))
		}

		super.visit(n, arg)
	}

	private boolean checkThreshold(Map<Boolean, List<MethodDeclaration>> map) {

		def trueSize = map[true] ? map[true].size() : 0
		def falseSize = map[false] ? map[false].size() : 0
		def allSize = trueSize + falseSize

		def match = false
		switch (threshold) {
			case threshold.all:
				match |= (trueSize == allSize && allSize > 1)
				break;
			case threshold.half:
				match |= (trueSize.toBigDecimal() >= (allSize / 2) && allSize > 3)
				break;
			case threshold.third:
				match |= (trueSize.toBigDecimal() >= (allSize / 3) && allSize > 5)
				break;
		}
		return match
	}

	private static boolean hasBodySizeOne(MethodDeclaration method) {
		return Optional.ofNullable(method.body)
				.filter { it.stmts.size() == 1 }
				.isPresent()
	}

	private static boolean useSameParametersForMethodInvocation(MethodDeclaration method) {
		def statement = method.body.stmts[0]
		if (statement instanceof ReturnStmt) {
			def expr = statement.expr
			if (expr instanceof MethodCallExpr) {
				def argsExpr = expr.args.collect { it.toStringWithoutComments() }
				def argsMethod = method.parameters.collect { it.id.name }
				return argsExpr.containsAll(argsMethod)
			}
		}
		return false
	}

	private static boolean hasComplexityOfOne(MethodDeclaration it) {
		MethodHelper.calcMcCabe(it) == 1
	}

	private static boolean hasNoFilteredAnnotations(MethodDeclaration method) {
		method.annotations.stream().noneMatch { it.name.name == "Bean" }
	}

}

