package io.gitlab.arturbosch.smartsmells.common.visitor

import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.BlockStmt
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethod

/**
 * @author artur
 */
abstract class MethodMetricVisitor<T extends DetectionResult> extends Visitor<T> {

	int threshold

	MethodMetricVisitor(int threshold) {
		this.threshold = threshold
	}

	@Override
	void visit(ConstructorDeclaration node, Resolver arg) {
		visitBlock(Optional.ofNullable(node.body), node)
	}

	private void visitBlock(Optional<BlockStmt> blockStmt, BodyDeclaration body) {
		blockStmt.map { it.statements }
				.filter { byThreshold(body) }
				.ifPresent { addSmell(body) }
	}

	@Override
	void visit(MethodDeclaration node, Resolver arg) {
		visitBlock(node.body, node)
	}

	protected LongMethod newLongMethod(BodyDeclaration n, int size) {
		if (n instanceof MethodDeclaration)
			longMethodIntern(n.nameAsString, n.declarationAsString, n, size)
		else {
			def node = (ConstructorDeclaration) n
			longMethodIntern(node.nameAsString, node.declarationAsString, n, size)
		}
	}

	private LongMethod longMethodIntern(String name, String signature, BodyDeclaration n, int size) {
		new LongMethod(name, signature, size, threshold,
				SourceRange.fromNode(n), SourcePath.of(relativePath), ElementTarget.METHOD)
	}

	protected abstract byThreshold(BodyDeclaration n)

	protected abstract addSmell(BodyDeclaration n)
}
