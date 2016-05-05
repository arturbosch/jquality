package com.gitlab.artismarti.smartsmells.godclass

import com.gitlab.artismarti.smartsmells.common.Defaults
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Smell
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class GodClassDetector extends Detector<GodClass> {

	int wmcThreshold
	int atfdThreshold
	double tccThreshold

	GodClassDetector(
			int wmcThreshold = Defaults.WEIGHTED_METHOD_COUNT,
			int atfdThreshold = Defaults.ACCESS_TO_FOREIGN_DATA,
			double tccThreshold = Defaults.TIED_CLASS_COHESION) {

		this.wmcThreshold = wmcThreshold
		this.atfdThreshold = atfdThreshold
		this.tccThreshold = tccThreshold
	}

	@Override
	protected Visitor getVisitor(Path path) {
		return new GodClassVisitor(atfdThreshold, wmcThreshold, tccThreshold, path)
	}

	@Override
	protected Smell getType() {
		return Smell.GOD_CLASS
	}
}
