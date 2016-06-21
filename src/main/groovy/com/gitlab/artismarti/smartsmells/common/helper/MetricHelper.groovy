package com.gitlab.artismarti.smartsmells.common.helper

import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.gitlab.artismarti.smartsmells.common.visitor.CyclomaticComplexityVisitor

/**
 * @author artur
 */
final class MetricHelper {

	static int mcCabe(BodyDeclaration n) {
		def complexityVisitor = new CyclomaticComplexityVisitor()
		n.accept(complexityVisitor, null)
		return complexityVisitor.mcCabeComplexity + 1
	}

	static int wmc(ClassOrInterfaceDeclaration n) {
		MethodHelper.filterAnonymousMethods(NodeHelper.findMethods(n))
				.stream()
				.mapToInt { mcCabe(it) }
				.sum()
	}

}
