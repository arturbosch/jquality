package io.gitlab.arturbosch.jpal.ast

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.visitors.LocaleVariableVisitor

import java.util.stream.Collectors

/**
 * Operates on method declarations to find locale variables.
 *
 * @author artur
 */
@CompileStatic
class LocaleVariableHelper {

	/**
	 * Searches for locale variable declarations withing all given methods.
	 *
	 * @param methodDeclarations given methods
	 * @return a list of locale variables
	 */
	static List<VariableDeclarationExpr> find(List<MethodDeclaration> methodDeclarations) {
		return methodDeclarations.stream().map { find(it) }
				.flatMap { it.stream() }
				.collect(Collectors.toList())
	}

	/**
	 * Searches for locale variable declarations within given method.
	 *
	 * @param methodDeclaration given method
	 * @return a set of locale variables
	 */
	static Set<VariableDeclarationExpr> find(MethodDeclaration methodDeclaration) {
		def visitor = new LocaleVariableVisitor()
		methodDeclaration.accept(visitor, null)
		return visitor.variables
	}
}
