package io.gitlab.arturbosch.smartsmells.smells.messagechain

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.MethodCallExpr
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.internal.Printer
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.jpal.resolution.symbols.WithPreviousSymbolReference
import io.gitlab.arturbosch.smartsmells.common.Visitor

import java.util.stream.Collectors

/**
 * @author artur
 */
class MessageChainVisitor extends Visitor<MessageChain> {

	private int chainSizeThreshold

	private Map<String, MethodCallExpr> methodCallExprMap = new HashMap<>()

	MessageChainVisitor(int chainSizeThreshold) {
		this.chainSizeThreshold = chainSizeThreshold
	}

	@Override
	void visit(CompilationUnit n, Resolver resolver) {
		super.visit(n, resolver)

		methodCallExprMap.entrySet().stream()
				.filter {
			def reference = resolver.resolve(it.value.name, info).orElse(null) as WithPreviousSymbolReference
			reference && !reference.isBuilderPattern()
		}.collect {

			new MessageChain(it.value.toString(Printer.NO_COMMENTS), extractSourceString(it.value),
					it.value.nameAsString, countOccurrences(it.key, "."), chainSizeThreshold,
					SourcePath.of(path), SourceRange.fromNode(it.value)

			)
		}.each { smells.add(it) }
	}

	private static String extractSourceString(MethodCallExpr it) {
		it.toString(Printer.NO_COMMENTS).split("\\.")[0].replace("(", "").replace(")", "")
	}

	@Override
	void visit(MethodCallExpr n, Resolver resolver) {
		def linkedExpr = extractExpressionNames(n)
		def expr = filterCollectionGets(linkedExpr)
		def count = countOccurrences(expr, ".")

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
		super.visit(n, resolver)
	}

	private static String filterCollectionGets(String expressions) {
		Arrays.stream(expressions.split("\\."))
				.filter { it != "get" }
				.collect(Collectors.joining("."))
	}

	def extractExpressionNames(MethodCallExpr n) {
		if (!n.scope.isPresent() || !(n.scope.get() instanceof MethodCallExpr)) return n.nameAsString
		return extractExpressionNames((MethodCallExpr) n.scope.get()) + "." + n.nameAsString
	}

	private static countOccurrences(String source, String pattern) {
		if (source.isEmpty() || pattern.isEmpty()) {
			return 0
		}

		int count = 0
		for (int pos = 0; (pos = source.indexOf(pattern, pos)) != -1; count++) {
			pos += pattern.length()
		}

		return count + 1
	}
}
