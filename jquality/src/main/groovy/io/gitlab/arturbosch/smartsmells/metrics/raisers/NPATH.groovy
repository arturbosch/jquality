package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.CallableDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.DoStmt
import com.github.javaparser.ast.stmt.ForStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.stmt.WhileStmt
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.visitor.InternalVisitor
import io.gitlab.arturbosch.smartsmells.metrics.MethodInfo
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import io.gitlab.arturbosch.smartsmells.metrics.Metrics
import io.gitlab.arturbosch.smartsmells.smells.deadcode.MutableInt

@CompileStatic
class NPATH implements MethodMetricListener {

	static final String NPATH = "NPATH"

	@Override
	void raise(CallableDeclaration callable, MethodInfo info, Resolver resolver) {
		def visitor = new NPathVisitor()
		callable.accept(visitor, resolver)
		info?.addMetric(Metric.of(NPATH, visitor.npath))
	}
}

@CompileStatic
class NPathVisitor extends InternalVisitor {

	int npath

	@Override
	void visit(MethodDeclaration n, Resolver arg) {
		super.visit(n, arg)
		n.body.ifPresent {
			npath = it.statements
					.inject(1) { acc, item -> acc * (complexityLookupTable[item] ?: 0) } as int
		}
	}

	@Override
	void visit(IfStmt n, Resolver arg) {
		super.visit(n, arg)
		int conditionComplexity = Metrics.booleanComplexity(n.condition)
		int thenComplexity = asStatementSequence(n.thenStmt)
				.inject(1) { acc, item -> acc + getComplexityForStmt(item) } as int
		int elseComplexity = n.elseStmt
				.map { asStatementSequence(it) }
				.map { it.inject(1) { acc, item -> acc + getComplexityForStmt(item as Statement) } }
				.orElse(1) as int
		complexityLookupTable[n] = conditionComplexity + thenComplexity + elseComplexity
	}

	@Override
	void visit(WhileStmt n, Resolver arg) {
		super.visit(n, arg)
		int conditionComplexity = Metrics.booleanComplexity(n.condition)
		int thenComplexity = asStatementSequence(n.body)
				.inject(1) { acc, item -> acc + getComplexityForStmt(item) } as int
		complexityLookupTable[n] = conditionComplexity + thenComplexity + 1
	}

	@Override
	void visit(DoStmt n, Resolver arg) {
		super.visit(n, arg)
		int conditionComplexity = Metrics.booleanComplexity(n.condition)
		int thenComplexity = asStatementSequence(n.body)
				.inject(1) { acc, item -> acc + getComplexityForStmt(item) } as int
		complexityLookupTable[n] = conditionComplexity + thenComplexity + 1
	}

	@Override
	void visit(ForStmt n, Resolver arg) {
		super.visit(n, arg)
		int conditionComplexity = n.compare.map { Metrics.booleanComplexity(it) }.orElse(0) as int
		int thenComplexity = asStatementSequence(n.body)
				.inject(1) { acc, item -> acc + getComplexityForStmt(item) } as int
		complexityLookupTable[n] = conditionComplexity + thenComplexity + 1
	}

	private Map<Statement, Integer> complexityLookupTable = new HashMap<>()

	private int getComplexityForStmt(Statement statement) {
		return complexityLookupTable.get(statement) ?: 0
	}

	private static List<Statement> asStatementSequence(Statement blockOrSingle) {
		if (blockOrSingle instanceof BlockStmt) {
			return blockOrSingle.statements
		}
		return Collections.singletonList(blockOrSingle)
	}
}

@CompileStatic
class BooleanComplexityVisitor extends VoidVisitorAdapter<MutableInt> {
	static final BooleanComplexityVisitor INSTANCE = new BooleanComplexityVisitor()

	static final Set<BinaryExpr.Operator> allowedOperators =
			[BinaryExpr.Operator.AND, BinaryExpr.Operator.OR].toSet()

	static int calculate(Expression expression) {
		def wrapper = new MutableInt()
		expression.accept(INSTANCE, wrapper)
		return wrapper.value
	}

	@Override
	void visit(BinaryExpr n, MutableInt arg) {
		if (n.getOperator() in allowedOperators) {
			arg.increment()
		}
		super.visit(n, arg)
	}
}
