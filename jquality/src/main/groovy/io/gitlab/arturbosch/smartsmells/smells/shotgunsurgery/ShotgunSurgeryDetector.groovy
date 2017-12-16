package io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.ClassHelper
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.raisers.CC_CM
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget

/**
 * @author Artur Bosch
 */
@CompileStatic
class ShotgunSurgeryDetector extends Detector<ShotgunSurgery> {

	private int cc
	private int cm

	ShotgunSurgeryDetector(int cc = Defaults.CHANGING_CLASSES, int cm = Defaults.CHANGING_METHODS) {
		this.cm = cm
		this.cc = cc
	}

	@Override
	protected Visitor getVisitor() {
		return new ShotgunSurgeryVisitor(cc, cm)
	}

	@Override
	Smell getType() {
		return Smell.SHOTGUN_SURGERY
	}
}

@CompileStatic
class ShotgunSurgeryVisitor extends Visitor<ShotgunSurgery> {

	private int ccThreshold
	private int cmThreshold

	ShotgunSurgeryVisitor(int cc, int cm) {
		this.cmThreshold = cm
		this.ccThreshold = cc
	}

	@Override
	void visit(ClassOrInterfaceDeclaration n, Resolver resolver) {
		if (isEmpty(n)) return

		def clazz = infoForClass(n)

		def cc = clazz?.getMetric(CC_CM.COUNT_CLASSES)?.value ?: 0
		def cm = clazz?.getMetric(CC_CM.COUNT_METHODS)?.value ?: 0

		if (cc > ccThreshold && cm > cmThreshold) {
			smells.add(new ShotgunSurgery(n.nameAsString, ClassHelper.createFullSignature(n),
					cc, cm, ccThreshold, cmThreshold, SourcePath.of(info),
					SourceRange.fromNode(n), ElementTarget.CLASS))
		}
		super.visit(n, resolver)
	}
}
