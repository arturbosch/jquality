package io.gitlab.arturbosch.smartsmells.common.visitor

import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.BlockStmt
import io.gitlab.arturbosch.smartsmells.common.Smelly
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.common.helper.BadSmellHelper
import io.gitlab.arturbosch.smartsmells.common.source.SourcePath
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethod

import java.nio.file.Path

/**
 * @author artur
 */
abstract class MethodMetricVisitor<T extends Smelly> extends Visitor<T> {

	int threshold

	MethodMetricVisitor(int threshold, Path path) {
		super(path)
		this.threshold = threshold
	}

	@Override
	void visit(ConstructorDeclaration node, Object arg) {
		visitBlock(Optional.ofNullable(node.block), node)
	}

	private void visitBlock(Optional<BlockStmt> blockStmt, BodyDeclaration body) {
		blockStmt.map { it.stmts }
				.filter { byThreshold(body) }
				.ifPresent { addSmell(body) }
	}

	@Override
	void visit(MethodDeclaration node, Object arg) {
		visitBlock(Optional.ofNullable(node.body), node)
	}

	protected LongMethod newLongMethod(BodyDeclaration n, int size) {
		if (n instanceof MethodDeclaration)
			longMethodIntern(n.declarationAsString, n.name, n.getDeclarationAsString(false, false, true), n, size)
		else {
			def node = (ConstructorDeclaration) n
			longMethodIntern(node.declarationAsString, node.name, node.getDeclarationAsString(false, false, true), n, size)
		}
	}

	private LongMethod longMethodIntern(String header, String name, String signature, BodyDeclaration n, int size) {
		new LongMethod(header, name, signature, size, threshold,
				BadSmellHelper.createSourceRangeFromNode(n), SourcePath.of(path)
		)
	}

	protected abstract byThreshold(BodyDeclaration n)

	protected abstract addSmell(BodyDeclaration n)
}
