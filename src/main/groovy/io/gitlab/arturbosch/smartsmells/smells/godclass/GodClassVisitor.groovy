package io.gitlab.arturbosch.smartsmells.smells.godclass

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.metrics.Metrics

import java.nio.file.Path

/**
 * GodClasses := ATFD > 5 ∧ WMC > 43 ∧ TCC < 0.33
 *
 * Metric proposed by:
 * R. Marinescu, Measurement and quality in object-oriented design, Ph.D. thesis in
 * the Faculty of Automatics and Computer Science of the Politehnica University of
 * Timisoara, 2003.
 *
 * proposed by http://www.cs.ubbcluj.ro/~studia-i/2011-4/03-Serban.pdf
 *
 * GodClasses := ((ATFD, TopValues(20%)) ∧ (ATFD, HigherThan(4))) ∧
 * ((WMC, HigherThan(20)) ∨ (TCC, LowerThan(0.33)).
 *
 * @author artur
 */
class GodClassVisitor extends Visitor<GodClass> {

	private int accessToForeignDataThreshold
	private int weightedMethodCountThreshold
	private double tiedClassCohesionThreshold

	GodClassVisitor(int accessToForeignDataThreshold,
					int weightedMethodCountThreshold,
					double tiedClassCohesionThreshold) {
		this.accessToForeignDataThreshold = accessToForeignDataThreshold
		this.weightedMethodCountThreshold = weightedMethodCountThreshold
		this.tiedClassCohesionThreshold = tiedClassCohesionThreshold
	}

	@Override
	void visit(CompilationUnit n, Resolver resolver) {

		def classes = n.getNodesByType(ClassOrInterfaceDeclaration.class)

		classes.each {
			def classVisitor = new InternalGodClassVisitor(path)
			classVisitor.visit(it)
		}

	}

	private class InternalGodClassVisitor extends VoidVisitorAdapter<Object> {

		private int atfd = 0
		private int wmc = 0
		private double tcc = 0.0
		private Path thePath

		InternalGodClassVisitor(Path thePath) {
			this.thePath = thePath
		}

		void visit(ClassOrInterfaceDeclaration n) {
			if (ClassHelper.isEmptyBody(n)) return
			if (ClassHelper.hasNoMethods(n)) return

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
			smells.add(new GodClass(n.nameAsString, ClassHelper.createFullSignature(n), wmc, tcc, atfd,
					weightedMethodCountThreshold, tiedClassCohesionThreshold,
					accessToForeignDataThreshold, SourcePath.of(thePath), SourceRange.fromNode(n)))
		}

	}
}

