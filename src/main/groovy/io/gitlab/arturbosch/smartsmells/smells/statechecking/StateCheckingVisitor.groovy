package io.gitlab.arturbosch.smartsmells.smells.statechecking

import com.github.javaparser.ast.expr.InstanceOfExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.stmt.SwitchStmt
import com.github.javaparser.utils.Pair
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.jpal.resolution.symbols.SymbolReference
import io.gitlab.arturbosch.smartsmells.common.Visitor
import sun.awt.util.IdentityArrayList

import java.util.function.BinaryOperator
import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
@CompileStatic
class StateCheckingVisitor extends Visitor<StateChecking> {

	final static String UNKNOWN_METHOD = "UNKNOWN_METHOD"

	private List<Statement> statementsFromElseBlock = new IdentityArrayList<>()

	@Override
	void visit(SwitchStmt n, Resolver arg) {
		if (n.selector instanceof NameExpr) {
			def symbol = (n.selector as NameExpr).name
			arg.resolve(symbol, info)
					.filter { SymbolReference reference -> reference.isVariable() }
					.ifPresent {
				List<String> cases = n.entries.stream().map {
					it.label.map { it.toString(Printer.NO_COMMENTS) }
							.orElse("default")
				}.collect(Collectors.toList())
				addStateSmell(n, cases)
			}
		}
		super.visit(n, arg)
	}

	@Override
	void visit(IfStmt n, Resolver arg) {
		if (statementsFromElseBlock.contains(n)) return
		def instanceOfExprs = checkInstanceOf(n)
		if (instanceOfExprs.size() > 1) {
			def cases = instanceOfExprs.collect { it.toString(Printer.NO_COMMENTS) }
			addStateSmell(n, cases)
		} else {
			def casesAndSymbols = collectSymbolsAndCases(n)
			def cases = casesAndSymbols.a
			def symbolMap = casesAndSymbols.b
			// cases > 2 on variables is a heuristic value to prevent simple (x < 1 and x >= 1 cases)
			if (cases.size() > 2 && symbolMap.size() > 0) {
				def symbol = mostUsedSymbolMeetsCaseCount(symbolMap, cases)
				if (symbol) {
					arg.resolve(symbol, info)
							.filter { SymbolReference reference -> reference.isVariable() }
							.ifPresent { addStateSmell(n, cases) }
				}
			}
		}
		n.elseStmt.ifPresent {
			statementsFromElseBlock.add(it)
		}
		super.visit(n, arg)
	}

	private static List<InstanceOfExpr> checkInstanceOf(IfStmt ifStmt,
														List<InstanceOfExpr> cases = new ArrayList<>()) {
		if (ifStmt.condition instanceof InstanceOfExpr) {
			cases.add(ifStmt.condition as InstanceOfExpr)
			ifStmt.elseStmt.ifPresent {
				if (it instanceof IfStmt) {
					checkInstanceOf((it as IfStmt), cases)
				}
			}
		}
		return cases
	}

	private static SimpleName mostUsedSymbolMeetsCaseCount(Map<SimpleName, Integer> symbolMap,
														   List<String> cases) {
		symbolMap.entrySet().stream().reduce(
				new BinaryOperator<Map.Entry<SimpleName, Integer>>() {
					@Override
					Map.Entry<SimpleName, Integer> apply(Map.Entry<SimpleName, Integer> e1,
														 Map.Entry<SimpleName, Integer> e2) {
						return e1.value >= e2.value ? e1 : e2
					}
				}
		).map { it.value == cases.size() ? it.key : null }.orElse(null)
	}

	private static Pair<List<String>, Map<SimpleName, Integer>> collectSymbolsAndCases(
			IfStmt node, List<String> cases = new ArrayList<>(),
			Map<SimpleName, Integer> map = new HashMap<>()) {
		cases.add(node.condition.toString(Printer.NO_COMMENTS))
		node.condition.getNodesByType(SimpleName.class)
				.each { map.merge(it, 1, { Integer v1, Integer v2 -> v1 + v2 }) }
		node.elseStmt.ifPresent {
			if (it instanceof IfStmt) {
				collectSymbolsAndCases((it as IfStmt), cases, map)
			}
		}
		return new Pair(cases, map)
	}

	private void addStateSmell(Statement n, List<String> cases) {
		def methodName = NodeHelper.findDeclaringMethod(n)
				.map { it.nameAsString }
				.orElse(UNKNOWN_METHOD)
		def stateCheck = new StateChecking(methodName, cases, SourcePath.of(path), SourceRange.fromNode(n))
		smells.add(stateCheck)
	}
}
