package io.gitlab.arturbosch.smartsmells.smells.featureenvy

import groovy.transform.Immutable
import io.gitlab.arturbosch.smartsmells.config.Defaults

/**
 * @author artur
 */
@Immutable
class FeatureEnvyFactor {

	double threshold
	double base
	double weight

	static FeatureEnvyFactor newInstance(double threshold = Defaults.FEATURE_ENVY_FACTOR,
										 double base = Defaults.FEATURE_ENVY_BASE,
										 double weight = Defaults.FEATURE_ENVY_WEIGHT) {
		return new FeatureEnvyFactor(threshold, base, weight)
	}
}
