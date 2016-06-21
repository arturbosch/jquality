package com.gitlab.artismarti.smartsmells.smells.featureenvy

import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path
/**
 * @author artur
 */
class FeatureEnvyDetector extends Detector<FeatureEnvy> {

	private FeatureEnvyFactor factor
	private boolean ignoreStatic


	FeatureEnvyDetector(FeatureEnvyFactor factor = FeatureEnvyFactor.newInstance(),
	                    boolean ignoreStatic = false) {
		this.factor = factor
		this.ignoreStatic = ignoreStatic
	}

	@Override
	protected Visitor getVisitor(Path path) {
		return new FeatureEnvyVisitor(path, factor, ignoreStatic)
	}

	@Override
	Smell getType() {
		return Smell.FEATURE_ENVY
	}
}
