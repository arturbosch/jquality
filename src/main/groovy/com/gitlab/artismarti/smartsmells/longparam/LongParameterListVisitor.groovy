package com.gitlab.artismarti.smartsmells.longparam

import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.Statement
import com.gitlab.artismarti.smartsmells.common.MethodHelper
import com.gitlab.artismarti.smartsmells.common.MethodMetricVisitor

import java.nio.file.Path
/**
 * @author artur
 */
class LongParameterListVisitor extends MethodMetricVisitor<LongParameterList> {

	LongParameterListVisitor(int threshold, Path path) {
		super(threshold, path)
	}

	@Override
	protected byThreshold(BodyDeclaration n, List<Statement> stmt) {
		def parameters = n instanceof ConstructorDeclaration ? n.parameters : ((MethodDeclaration) n).parameters
		return parameters.size() > threshold
	}

	@Override
	protected addSmell(BodyDeclaration n, List<Statement> it) {
		def parameters = MethodHelper.extractParameters(n)
		smells.add(new LongParameterList(newLongMethod(n, it),
				parameters.collect { it.toStringWithoutComments() }))
	}
}
