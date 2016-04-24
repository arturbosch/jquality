package com.gitlab.artismarti.smartsmells.featureenvy

import com.gitlab.artismarti.smartsmells.common.Defaults
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class FeatureEnvyDetector extends Detector<FeatureEnvy> {

	private double threshold

	FeatureEnvyDetector(double threshold = Defaults.FEATURE_ENVY_FACTOR) {
		this.threshold = threshold
	}

	@Override
	protected Visitor getVisitor(Path path) {
		return new FeatureEnvyVisitor(path, threshold)
	}

}
