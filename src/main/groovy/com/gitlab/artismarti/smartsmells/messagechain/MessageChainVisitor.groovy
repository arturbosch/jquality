package com.gitlab.artismarti.smartsmells.messagechain

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.MethodCallExpr
import com.gitlab.artismarti.smartsmells.common.BadSmellHelper
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.domain.SourcePath

import java.nio.file.Path

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

			new MessageChain(it.key, it.key.split("\\.")[0], it.value.name, countOccurrences(it.key, "get"), chainSizeThreshold,
					SourcePath.of(path), BadSmellHelper.createSourceRangeFromNode(it.value)

			)
		}.each { smells.add(it) }
	}

	@Override
	void visit(MethodCallExpr n, Object arg) {
		def expr = n.toStringWithoutComments()
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

	static def countOccurrences(String source, String pattern) {
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
