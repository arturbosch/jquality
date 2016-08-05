package io.gitlab.arturbosch.smartsmells.smells.featureenvy

import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Smell

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
