package io.gitlab.arturbosch.smartsmells.smells.nestedblockdepth

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.DoStmt
import com.github.javaparser.ast.stmt.ForStmt
import com.github.javaparser.ast.stmt.ForeachStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.SwitchStmt
import com.github.javaparser.ast.stmt.TryStmt
import com.github.javaparser.ast.stmt.WhileStmt
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.common.visitor.InternalVisitor
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
@CompileStatic
class NestedBlockDepthVisitor extends Visitor<NestedBlockDepth> {

	private int threshold

	NestedBlockDepthVisitor(int threshold) {
		this.threshold = threshold
	}

	@Override
	void visit(MethodDeclaration n, Resolver arg) {
		def visitor = new MethodDepthVisitor()
		visitor.visit(n, arg)
		if (visitor.isTooDeep()) {
			smells.add(new NestedBlockDepth(n.nameAsString, n.declarationAsString,
					visitor.maxDepth, threshold, SourceRange.fromNode(n), SourcePath.of(path), ElementTarget.METHOD))
		}
	}

	class MethodDepthVisitor extends InternalVisitor {

		int depth = 0
		int maxDepth = 0
		boolean tooDeep = false

		private inc() {
			depth++
			if (depth > threshold) {
				tooDeep = true
				if (depth > maxDepth) maxDepth = depth
			}
		}

		private dec() {
			depth--
		}

		@Override
		void visit(DoStmt n, Resolver arg) {
			inc()
			super.visit(n, arg)
			dec()
		}

		@Override
		void visit(IfStmt n, Resolver arg) {
			inc()
			super.visit(n, arg)
			dec()
		}

		@Override
		void visit(ForeachStmt n, Resolver arg) {
			inc()
			super.visit(n, arg)
			dec()
		}

		@Override
		void visit(ForStmt n, Resolver arg) {
			inc()
			super.visit(n, arg)
			dec()
		}

		@Override
		void visit(SwitchStmt n, Resolver arg) {
			inc()
			super.visit(n, arg)
			dec()
		}

		@Override
		void visit(WhileStmt n, Resolver arg) {
			inc()
			super.visit(n, arg)
			dec()
		}

		@Override
		void visit(TryStmt n, Resolver arg) {
			inc()
			super.visit(n, arg)
			dec()
		}
	}
}
