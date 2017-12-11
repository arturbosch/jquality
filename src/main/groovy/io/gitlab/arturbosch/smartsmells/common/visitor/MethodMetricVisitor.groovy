package io.gitlab.arturbosch.smartsmells.common.visitor

import com.github.javaparser.ast.body.CallableDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult

/**
 * @author artur
 */
@CompileStatic
abstract class MethodMetricVisitor<T extends DetectionResult> extends Visitor<T> {

	ClassInfo current

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver arg) {
		current = infoForClass(n)
		super.visit(n, arg)
	}

	@Override
	void visit(ConstructorDeclaration node, Resolver arg) {
		callback(node, arg)
		super.visit(node, arg)
	}

	@Override
	void visit(MethodDeclaration node, Resolver arg) {
		callback(node, arg)
		super.visit(node, arg)
	}

	protected abstract void callback(CallableDeclaration n, Resolver arg)
}
