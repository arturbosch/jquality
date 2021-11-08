package io.gitlab.arturbosch.jpal.ast

import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.stmt.ReturnStmt
import com.github.javaparser.ast.stmt.Statement
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.visitors.MethodInvocationCountVisitor
import io.gitlab.arturbosch.jpal.internal.Validate

import java.util.stream.Collectors

/**
 * Provides static helper methods to use on method declarations.
 *
 * @author artur
 */
@CompileStatic
final class MethodHelper {

	/**
	 * Extracts parameters from a method or constructor.
	 *
	 * @param n body declaration e.g. method or constructor
	 * @return list of parameters or empty
	 */
	static List<Parameter> extractParameters(BodyDeclaration n) {
		Validate.notNull(n)
		return (n instanceof ConstructorDeclaration ? n.parameters :
				n instanceof MethodDeclaration ? n.parameters : Collections.emptyList()) as List<Parameter>
	}

	/**
	 * Tests if the given method declaration is a getter or setter.
	 * A getter has just a single return statement and a setter a single
	 * assignment.
	 *
	 * @param methodDeclaration given method
	 * @return true if getter or setter
	 */
	static boolean isGetterOrSetter(MethodDeclaration methodDeclaration) {
		return Validate.notNull(methodDeclaration).body
				.map { it.statements }
				.filter { it.size() == 1 }
				.map { it[0] }
				.map { isGetter(it) || isSetter(it) }
				.orElse(Boolean.FALSE)
	}

	private static boolean isSetter(Statement statement) {
		if (statement instanceof ExpressionStmt) {
			def expression = ((ExpressionStmt) statement).expression
			if (expression instanceof AssignExpr) {
				return true
			}
		}
		return false
	}

	private static boolean isGetter(Statement statement) {
		if (statement instanceof ReturnStmt) {
			return ((ReturnStmt) statement).expression
					.filter { it instanceof NameExpr }
					.map { Boolean.TRUE }
					.orElse(Boolean.FALSE)
		}
		return false
	}

	/**
	 * Counts all method invocations for given method.
	 * @param it the method
	 * @return amount of calls
	 */
	static int getAllMethodInvocations(MethodDeclaration it) {
		def visitor = new MethodInvocationCountVisitor()
		return executeVisitor(it, visitor)
	}

	private static int executeVisitor(MethodDeclaration it, MethodInvocationCountVisitor visitor) {
		it.accept(visitor, null)
		visitor.count
	}

	/**
	 * Counts all method invocations on a variable with given search name within given method.
	 * @param searchedName entities name to count invocations for
	 * @param it the method
	 * @return amount of entity calls
	 */
	static int getAllMethodInvocationsForEntityWithName(String searchedName, MethodDeclaration it) {
		def visitor = new MethodInvocationCountVisitor(searchedName)
		return executeVisitor(it, visitor)
	}

	/**
	 * Tests if the given method's body measured in statements(!) is bigger than the given threshold
	 * @param threshold
	 * @param methodDeclaration
	 * @return
	 */
	static boolean sizeBiggerThan(int threshold, MethodDeclaration methodDeclaration) {
		return methodDeclaration.body.filter { it.statements.size() > threshold }.isPresent()
	}

	/**
	 * Use this method if anonymous classes produce duplicates for specific smells which
	 * occur in methods.
	 * @param methods list of all methods from a class
	 * @return filtered methods
	 */
	static List<MethodDeclaration> filterAnonymousMethods(List<MethodDeclaration> methods) {
		return methods.stream()
				.filter { !isAnonymousMethod(it) }
				.collect(Collectors.toList())
	}

	/**
	 * Tests if given method is anonymous meaning it's parent is an object creation expression.
	 *
	 * @param methodDeclaration given method
	 * @return true if parent is an object creation expression
	 */
	static boolean isAnonymousMethod(MethodDeclaration methodDeclaration) {
		return methodDeclaration.getParentNode()
				.filter { it instanceof ObjectCreationExpr }
				.isPresent()
	}
}
