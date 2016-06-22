package com.gitlab.artismarti.smartsmells.smells.godclass

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.gitlab.artismarti.smartsmells.common.Visitor
import com.gitlab.artismarti.smartsmells.common.helper.BadSmellHelper
import com.gitlab.artismarti.smartsmells.metrics.Metrics
import com.gitlab.artismarti.smartsmells.common.helper.TypeHelper
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourcePosition
import com.gitlab.artismarti.smartsmells.common.source.SourceRange

import java.nio.file.Path

/**
 * GodClasses := ((ATFD, TopValues(20%)) ∧ (ATFD, HigherThan(4))) ∧
 * ((WMC, HigherThan(20)) ∨ (TCC, LowerThan(0.33)).
 *
 * Metric proposed by:
 * R. Marinescu, Measurement and quality in object-oriented design, Ph.D. thesis in
 * the Faculty of Automatics and Computer Science of the Politehnica University of
 * Timisoara, 2003.
 *
 * @author artur
 */
class GodClassVisitor extends Visitor<GodClass> {

	private int accessToForeignDataThreshold
	private int weightedMethodCountThreshold
	private double tiedClassCohesionThreshold

	GodClassVisitor(int accessToForeignDataThreshold,
	                int weightedMethodCountThreshold,
	                double tiedClassCohesionThreshold,
	                Path path) {
		super(path)
		this.accessToForeignDataThreshold = accessToForeignDataThreshold
		this.weightedMethodCountThreshold = weightedMethodCountThreshold
		this.tiedClassCohesionThreshold = tiedClassCohesionThreshold
	}

	@Override
	void visit(CompilationUnit n, Object arg) {

		def classes = ASTHelper.getNodesByType(n, ClassOrInterfaceDeclaration.class)

		classes.each {
			def classVisitor = new InternalGodClassVisitor()
			classVisitor.visit(it)
		}

	}

	private class InternalGodClassVisitor extends VoidVisitorAdapter<Object> {

		private int atfd = 0
		private int wmc = 0
		private double tcc = 0.0

		void visit(ClassOrInterfaceDeclaration n) {
			if (TypeHelper.isEmptyBody(n)) return
			if (TypeHelper.hasNoMethods(n)) return

			tcc = Metrics.tcc(n)
			wmc = Metrics.wmc(n)
			atfd = Metrics.atfd(n)

			if (checkThresholds()) {
				addSmell(n)
			}
		}

		private boolean checkThresholds() {
			atfd > accessToForeignDataThreshold &&
					wmc > weightedMethodCountThreshold &&
					tcc < tiedClassCohesionThreshold
		}

		private boolean addSmell(ClassOrInterfaceDeclaration n) {
			smells.add(new GodClass(n.name, BadSmellHelper.createClassSignature(n), wmc, tcc, atfd,
					weightedMethodCountThreshold, tiedClassCohesionThreshold,
					accessToForeignDataThreshold, SourcePath.of(path),
					SourceRange.of(SourcePosition.of(n.beginLine, n.beginColumn),
							SourcePosition.of(n.endLine, n.endColumn))))
		}

	}
}

