package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.metrics.Metric

/**
 * @author Artur Bosch
 */
@CompileStatic
class NOAV implements MethodMetricRaiser {

	public static final String NUMBER_OF_ACCESSED_VARIABLES = "NumberOfAccessedVariables"

	@Override
	String name() {
		return NUMBER_OF_ACCESSED_VARIABLES
	}

	@Override
	Metric raise(MethodDeclaration method, Resolver resolver) {
		def count = new AccessedVariablesVisitor().count(method, resolver)
		return Metric.of(NUMBER_OF_ACCESSED_VARIABLES, count)
	}
}

@CompileStatic
class AccessedVariablesVisitor extends VoidVisitorAdapter<Resolver> {

	private final Set<String> variables = new HashSet<>()
	private final Set<String> parameters = new HashSet<>()
	private final Set<String> fields = new HashSet<>()

	int count(MethodDeclaration method, Resolver resolver) {
		super.visit(method, resolver)
		return variables.size() + parameters.size() + fields.size()
	}

	@Override
	void visit(VariableDeclarationExpr n, Resolver arg) {
		variables.addAll(n.variables*.nameAsString)
	}

	@Override
	void visit(Parameter n, Resolver arg) {
		parameters.add(n.nameAsString)
	}

	@Override
	void visit(FieldAccessExpr n, Resolver arg) {
		fields.add(n.nameAsString)
	}
}
