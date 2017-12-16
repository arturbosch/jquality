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
import io.gitlab.arturbosch.smartsmells.metrics.MethodInfo
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.metrics.Metrics
import io.gitlab.arturbosch.smartsmells.metrics.internal.LinesOfCode

/**
 * @author Artur Bosch
 */
@CompileStatic
trait MethodMetricListener {

	int priority() {
		return 0
	}

	abstract void raise(CallableDeclaration callable, MethodInfo info, Resolver resolver)
}

@CompileStatic
class MLOC implements MethodMetricListener {

	@Override
	void raise(CallableDeclaration callable, MethodInfo info, Resolver resolver) {
		def loc = new LinesOfCode()
		loc.analyze(LOC.NL.split(callable.toString()))
		[Metric.of(LOC.LOC, loc.source + loc.blank + loc.comment),
		 Metric.of(LOC.SLOC, loc.source),
		 Metric.of(LOC.CLOC, loc.comment),
		 Metric.of(LOC.LLOC, loc.logical),
		 Metric.of(LOC.BLOC, loc.blank)].each {
			info.addMetric(it)
		}
	}
}

@CompileStatic
class NOP implements MethodMetricListener {

	static final String NUMBER_OF_PARAMETERS = "NumberOfParameters"

	@Override
	void raise(CallableDeclaration callable, MethodInfo info, Resolver resolver) {
		info?.addMetric(Metric.of(NUMBER_OF_PARAMETERS, callable.parameters.size()))
	}
}

@CompileStatic
class NOAV implements MethodMetricListener {

	static final String NUMBER_OF_ACCESSED_VARIABLES = "NumberOfAccessedVariables"

	@Override
	void raise(CallableDeclaration callable, MethodInfo info, Resolver resolver) {
		info?.addMetric(Metric.of(NUMBER_OF_ACCESSED_VARIABLES,
				new AccessedVariablesVisitor().count(callable, resolver)))
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
class CYCLO implements MethodMetricListener {

	static final String CYCLOMATIC_COMPLEXITY = "CYCLO"

	@Override
	void raise(CallableDeclaration callable, MethodInfo info, Resolver resolver) {
		info?.addMetric(Metric.of(CYCLOMATIC_COMPLEXITY, Metrics.mcCabe(callable)))
	}
}

@CompileStatic
class MAXNESTING implements MethodMetricListener {

	static final String MAXNESTING = "MAXNESTING"

	@Override
	void raise(CallableDeclaration callable, MethodInfo info, Resolver resolver) {
		info?.addMetric(Metric.of(MAXNESTING, new MethodDepthVisitor().findMaxDepth(callable, resolver)))
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

	int findMaxDepth(CallableDeclaration n, Resolver arg) {
		n instanceof MethodDeclaration ?
				visit(n, arg) :
				visit((ConstructorDeclaration) n, arg)
		return maxDepth
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
