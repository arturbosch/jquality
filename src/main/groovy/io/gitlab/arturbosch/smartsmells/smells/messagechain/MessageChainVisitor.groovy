package io.gitlab.arturbosch.smartsmells.smells.messagechain

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.MethodCallExpr
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.common.helper.BadSmellHelper
import io.gitlab.arturbosch.smartsmells.common.source.SourcePath

import java.nio.file.Path
import java.util.stream.Collectors

/**
 * @author artur
 */
class MessageChainVisitor extends Visitor<MessageChain> {

	private int chainSizeThreshold

	private Map<String, MethodCallExpr> methodCallExprMap = new HashMap<>()

	MessageChainVisitor(Path path, int chainSizeThreshold) {
		super(path)
		this.chainSizeThreshold = chainSizeThreshold
	}

	@Override
	void visit(CompilationUnit n, Object arg) {
		super.visit(n, arg)

		methodCallExprMap.entrySet().stream()
				.collect {

			new MessageChain(it.value.toStringWithoutComments(), extractSourceString(it.value),
					it.value.name, countOccurrences(it.key, "get"), chainSizeThreshold,
					SourcePath.of(path), BadSmellHelper.createSourceRangeFromNode(it.value)

			)
		}.each { smells.add(it) }
	}

	private static String extractSourceString(MethodCallExpr it) {
		it.toStringWithoutComments().split("\\.")[0].replace("(", "").replace(")", "")
	}

	@Override
	void visit(MethodCallExpr n, Object arg) {
		def linkedExpr = extractExpressionNames(n)
		def expr = filterCollectionGets(linkedExpr)
		def count = countOccurrences(expr, "get")

		boolean found = false
		if (count >= chainSizeThreshold) {
			methodCallExprMap.keySet().each {
				if (it.contains(expr)) {
					found = true
				}
			}
			if (!found) {
				methodCallExprMap.put(expr, n)
			}
		}
		super.visit(n, arg)
	}

	private static String filterCollectionGets(String expressions) {
		Arrays.stream(expressions.split("\\."))
				.filter { it != "get" }
				.collect(Collectors.joining("."))
	}

	def extractExpressionNames(MethodCallExpr n) {
		if (n.scope == null || !(n.scope instanceof MethodCallExpr)) return n.name
		return extractExpressionNames((MethodCallExpr) n.scope) + "." + n.name
	}

	private static def countOccurrences(String source, String pattern) {
		if (source.isEmpty() || pattern.isEmpty()) {
			return 0;
		}

		int count = 0;
		for (int pos = 0; (pos = source.indexOf(pattern, pos)) != -1; count++) {
			pos += pattern.length();
		}

		return count;
	}
}
