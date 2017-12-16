package io.gitlab.arturbosch.smartsmells.smells.godclass

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfo
import io.gitlab.arturbosch.smartsmells.metrics.raisers.ATFD
import io.gitlab.arturbosch.smartsmells.metrics.raisers.TCC
import io.gitlab.arturbosch.smartsmells.metrics.raisers.WMC
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
@CompileStatic
class GodClassDetector extends Detector<GodClass> {

	private GodClassThreshold threshold

	GodClassDetector(GodClassThreshold threshold = new GodClassThreshold()) {
		this.threshold = threshold
	}

	@Override
	protected Visitor getVisitor() {
		return new GodClassVisitor(threshold)
	}

	@Override
	Smell getType() {
		return Smell.GOD_CLASS
	}
}

@Canonical
class GodClassThreshold {
	int WMC = 47
	double TCC = 0.33
	int ATFD = 5
}

/**
 * Detection Strategy proposed by:
 * R. Marinescu, Measurement and quality in object-oriented design, Ph.D. thesis in
 * the Faculty of Automatics and Computer Science of the Politehnica University of
 * Timisoara, 2003.
 *
 * http://www.simpleorientedarchitecture.com/how-to-identify-god-class-using-ndepend/
 *
 * (ATFD > Few) AND (WMC >= Very High) AND (TCC < One Third)
 *
 * @author Artur Bosch
 */
@CompileStatic
class GodClassVisitor extends Visitor<GodClass> {

	private GodClassThreshold threshold

	GodClassVisitor(GodClassThreshold threshold = new GodClassThreshold()) {
		this.threshold = threshold
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver arg) {
		if (isEmpty(n)) return

		ClassInfo classInfo = infoForClass(n)
		double tcc = classInfo?.getMetric(TCC.TIED_CLASS_COHESION)?.asDouble() ?: 1.00d
		int wmc = classInfo?.getMetric(WMC.WEIGHTED_METHOD_COUNT)?.value ?: 0
		int atfd = classInfo?.getMetric(ATFD.ACCESS_TO_FOREIGN_DATA)?.value ?: 0

		if (atfd > threshold.ATFD && wmc >= threshold.WMC && tcc < threshold.TCC) {
			smells.add(new GodClass(n.nameAsString, ClassHelper.createFullSignature(n), wmc, tcc, atfd,
					threshold.WMC, threshold.TCC, threshold.ATFD,
					SourcePath.of(info), SourceRange.fromNode(n), ElementTarget.CLASS))
		}
		super.visit(n, arg)
	}
}
