package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.stmt.Statement
import com.gitlab.artismarti.smartsmells.complexmethod.CyclomaticComplexityVisitor

/**
 * @author artur
 */
class MethodHelper {

	static int calcMcCabe(BodyDeclaration n) {
		return mcCabeIntern(n) + 1
	}

	static int calcMcCabeForStatement(Statement n) {
		return mcCabeIntern(n)
	}

	private static int mcCabeIntern(Node n) {
		def complexityVisitor = new CyclomaticComplexityVisitor()
		n.accept(complexityVisitor, null)
		return complexityVisitor.mcCabeComplexity
	}

	static List<Parameter> extractParameters(BodyDeclaration n) {
		return n instanceof ConstructorDeclaration ?
				n.parameters : ((MethodDeclaration) n).parameters
	}

}
