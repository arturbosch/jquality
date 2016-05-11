package com.gitlab.artismarti.smartsmells.featureenvy

import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path
/**
 * @author artur
 */
class FeatureEnvyDetector extends Detector<FeatureEnvy> {

	private FeatureEnvyFactor factor

	FeatureEnvyDetector(FeatureEnvyFactor factor = FeatureEnvyFactor.newInstance()) {
		this.factor = factor
	}

	@Override
	protected Visitor getVisitor(Path path) {
		return new FeatureEnvyVisitor(path, factor)
	}

	@Override
	Smell getType() {
		return Smell.FEATURE_ENVY
	}
}
