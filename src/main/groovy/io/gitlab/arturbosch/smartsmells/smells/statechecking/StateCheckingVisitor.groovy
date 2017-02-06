package io.gitlab.arturbosch.smartsmells.smells.statechecking

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.EnumDeclaration
import com.github.javaparser.ast.expr.InstanceOfExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.stmt.SwitchStmt
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.NodeHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.jpal.resolution.symbols.SymbolReference
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import sun.awt.util.IdentityArrayList

import java.util.function.BinaryOperator

/**
 * @author Artur Bosch
 */
@CompileStatic
class StateCheckingVisitor extends Visitor<StateChecking> {

	final static String UNKNOWN_METHOD = "UNKNOWN_METHOD"

	private String currentClassName = ""
	private List<Statement> statementsFromElseBlock = new IdentityArrayList<>()

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver arg) {
		currentClassName = ClassHelper.createFullSignature(n)
		super.visit(n, arg)
	}

	@Override
	void visit(EnumDeclaration n, Resolver arg) {
		currentClassName = n.nameAsString
		super.visit(n, arg)
	}

	@Override
	void visit(SwitchStmt n, Resolver arg) {
		if (n.selector instanceof NameExpr) {
			def symbol = (n.selector as NameExpr).name
			arg.resolve(symbol, info)
					.filter { SymbolReference reference -> reference.isVariable() }
					.ifPresent {
				List<String> cases = CasesCollector.ofSwitch(n)
				addStateSmell(n, cases, StateChecking.SUBTYPING)
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
			addStateSmell(n, cases, StateChecking.INSTANCE_OF)
		} else {
			def casesAndSymbols = CasesCollector.collectSymbolsAndCases(n)
			def cases = casesAndSymbols.a
			def symbolMap = casesAndSymbols.b
			// cases > 2 on variables is a heuristic value to prevent simple (x < 1 and x >= 1 cases)
			if (cases.size() > 2 && symbolMap.size() > 0) {
				def symbol = mostUsedSymbolMeetsCaseCount(symbolMap, cases)
				if (symbol) {
					arg.resolve(symbol, info)
							.filter { SymbolReference reference -> reference.isVariable() }
							.ifPresent { addStateSmell(n, cases, StateChecking.SUBTYPING) }
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

	private void addStateSmell(Statement n, List<String> cases, String type) {
		def methodName = NodeHelper.findDeclaringMethod(n)
				.map { it.declarationAsString }
				.orElse(UNKNOWN_METHOD)
		def signature = currentClassName + "#" + methodName
		def stateCheck = new StateChecking(signature, cases, type,
				SourcePath.of(path), SourceRange.fromNode(n), ElementTarget.LOCAL)
		smells.add(stateCheck)
	}
}
