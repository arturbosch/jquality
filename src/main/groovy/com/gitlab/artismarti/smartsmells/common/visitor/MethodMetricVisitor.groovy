package com.gitlab.artismarti.smartsmells.common.visitor

import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.Statement
import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import com.gitlab.artismarti.smartsmells.smells.longmethod.LongMethod

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
		blockStmt.map({ it.stmts })
				.filter({ byThreshold(body, it) })
				.ifPresent({ addSmell(body, it) }
		)
	}



	@Override
	void visit(MethodDeclaration node, Object arg) {
		visitBlock(Optional.ofNullable(node.body), node)
	}

	protected LongMethod newLongMethod(BodyDeclaration n, List<Statement> it) {
		if (n instanceof MethodDeclaration)
			longMethodIntern(n.declarationAsString, n.name, n.getDeclarationAsString(false, false, true), n, it)
		else {
			def node = (ConstructorDeclaration) n
			longMethodIntern(node.declarationAsString, node.name, node.getDeclarationAsString(false, false, true), n, it)
		}
	}

	private LongMethod longMethodIntern(String header, String name, String signature, BodyDeclaration n, List<Statement> it) {
		new LongMethod(header, name, signature, it.size(), threshold,
				SourceRange.of(n.getBeginLine(), n.getEndLine(), n.getBeginColumn(), n.getEndColumn()),
				SourcePath.of(path)
		)
	}

	protected abstract byThreshold(BodyDeclaration n, List<Statement> stmt)

	protected abstract addSmell(BodyDeclaration n, List<Statement> stmt)
}
