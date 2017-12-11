package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.CallableDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.stmt.DoStmt
import com.github.javaparser.ast.stmt.ForStmt
import com.github.javaparser.ast.stmt.ForeachStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.SwitchStmt
import com.github.javaparser.ast.stmt.TryStmt
import com.github.javaparser.ast.stmt.WhileStmt
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.visitor.InternalVisitor
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.metrics.Metrics

/**
 * @author Artur Bosch
 */
@CompileStatic
interface MethodMetricRaiser {

	String name()

	Metric raise(CallableDeclaration method, Resolver resolver)
}

@CompileStatic
class NOAV implements MethodMetricRaiser {

	public static final String NUMBER_OF_ACCESSED_VARIABLES = "NumberOfAccessedVariables"

	@Override
	String name() {
		return NUMBER_OF_ACCESSED_VARIABLES
	}

	@Override
	Metric raise(CallableDeclaration method, Resolver resolver) {
		def count = new AccessedVariablesVisitor().count(method, resolver)
		return Metric.of(NUMBER_OF_ACCESSED_VARIABLES, count)
	}
}

@CompileStatic
class AccessedVariablesVisitor extends VoidVisitorAdapter<Resolver> {

	private final Set<String> variables = new HashSet<>()
	private final Set<String> parameters = new HashSet<>()
	private final Set<String> fields = new HashSet<>()

	int count(CallableDeclaration method, Resolver resolver) {
		method instanceof MethodDeclaration ?
				visit(method, resolver) :
				visit((ConstructorDeclaration) method, resolver)
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

@CompileStatic
class CYCLO implements MethodMetricRaiser {

	static final String CYCLOMATIC_COMPLEXITY = "CYCLO"

	@Override
	String name() {
		return CYCLOMATIC_COMPLEXITY
	}

	@Override
	Metric raise(CallableDeclaration method, Resolver resolver) {
		def mcCabe = Metrics.mcCabe(method)
		return Metric.of(CYCLOMATIC_COMPLEXITY, mcCabe)
	}
}

@CompileStatic
class MAXNESTING implements MethodMetricRaiser {

	public static final String MAXNESTING = "MAXNESTING"

	@Override
	String name() {
		return MAXNESTING
	}

	@Override
	Metric raise(CallableDeclaration method, Resolver resolver) {
		def visitor = new MethodDepthVisitor()
		method instanceof MethodDeclaration ?
				visitor.visit(method, resolver) :
				visitor.visit((ConstructorDeclaration) method, resolver)
		return Metric.of(MAXNESTING, visitor.maxDepth)
	}
}

@CompileStatic
class MethodDepthVisitor extends InternalVisitor {

	int depth = 0
	int maxDepth = 0

	private inc() {
		depth++
		if (depth > maxDepth) {
			maxDepth = depth
		}
	}

	private dec() {
		depth--
	}

	@Override
	void visit(DoStmt n, Resolver arg) {
		inc()
		super.visit(n, arg)
		dec()
	}

	@Override
	void visit(IfStmt n, Resolver arg) {
		inc()
		super.visit(n, arg)
		dec()
	}

	@Override
	void visit(ForeachStmt n, Resolver arg) {
		inc()
		super.visit(n, arg)
		dec()
	}

	@Override
	void visit(ForStmt n, Resolver arg) {
		inc()
		super.visit(n, arg)
		dec()
	}

	@Override
	void visit(SwitchStmt n, Resolver arg) {
		inc()
		super.visit(n, arg)
		dec()
	}

	@Override
	void visit(WhileStmt n, Resolver arg) {
		inc()
		super.visit(n, arg)
		dec()
	}

	@Override
	void visit(TryStmt n, Resolver arg) {
		inc()
		super.visit(n, arg)
		dec()
	}
}
