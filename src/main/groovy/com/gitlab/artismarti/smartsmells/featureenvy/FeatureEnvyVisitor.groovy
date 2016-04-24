package com.gitlab.artismarti.smartsmells.featureenvy

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.gitlab.artismarti.smartsmells.common.NodeHelper
import com.gitlab.artismarti.smartsmells.common.TypeHelper
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class FeatureEnvyVisitor extends Visitor<FeatureEnvy> {

	private double threshold

	FeatureEnvyVisitor(Path path, double threshold) {
		super(path)
		this.threshold = threshold
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Object arg) {
		if (TypeHelper.isEmptyBody(n)) return
		if (TypeHelper.hasNoMethods(n)) return

		def methods = NodeHelper.findMethods(n)

		methods.each {
			println it.name
			def visitor = new MethodInvocationCountVisitor()
			it.accept(visitor, null)
			println visitor.count
		}
	}
}
