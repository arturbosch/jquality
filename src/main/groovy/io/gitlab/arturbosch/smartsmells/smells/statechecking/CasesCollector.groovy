package io.gitlab.arturbosch.smartsmells.smells.statechecking

import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.SwitchStmt
import com.github.javaparser.utils.Pair
import io.gitlab.arturbosch.jpal.internal.Printer

import java.util.stream.Collectors

/**
 * @author Artur Bosch
 */
final class CasesCollector {

	private CasesCollector() {
	}

	static List<String> ofSwitch(SwitchStmt stmt) {
		stmt.entries.stream().map {
			it.label.map { it.toString(Printer.NO_COMMENTS) }
					.orElse("default")
		}.collect(Collectors.toList())
	}

	static List<String> ofIf(IfStmt stmt) {
		collectSymbolsAndCases(stmt).a
	}

	static Pair<List<String>, Map<SimpleName, Integer>> collectSymbolsAndCases(IfStmt node,
																			   List<String> cases = new ArrayList<>(),
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
}
