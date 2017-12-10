package io.gitlab.arturbosch.smartsmells.common.visitor

import com.github.javaparser.ast.body.CallableDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.BlockStmt
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethod

/**
 * @author artur
 */
@CompileStatic
abstract class MethodMetricVisitor<T extends DetectionResult> extends Visitor<T> {

	int threshold

	MethodMetricVisitor(int threshold) {
		this.threshold = threshold
	}

	@Override
	void visit(ConstructorDeclaration node, Resolver arg) {
		visitBlock(Optional.ofNullable(node.body), node)
	}

	private void visitBlock(Optional<BlockStmt> blockStmt, CallableDeclaration body) {
		blockStmt.map { it.statements }
				.filter { byThreshold(body) }
				.ifPresent { addSmell(body) }
	}

	@Override
	void visit(MethodDeclaration node, Resolver arg) {
		visitBlock(node.body, node)
	}

	protected LongMethod newLongMethod(CallableDeclaration n, int size) {
		return new LongMethod(n.nameAsString, n.declarationAsString, size, threshold,
				SourceRange.fromNode(n), SourcePath.of(info), ElementTarget.METHOD)
	}

	protected abstract byThreshold(CallableDeclaration n)

	protected abstract addSmell(CallableDeclaration n)
}
