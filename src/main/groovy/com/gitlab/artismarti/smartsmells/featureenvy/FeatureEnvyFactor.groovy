package com.gitlab.artismarti.smartsmells.featureenvy

import com.gitlab.artismarti.smartsmells.common.Defaults
import groovy.transform.Immutable

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
