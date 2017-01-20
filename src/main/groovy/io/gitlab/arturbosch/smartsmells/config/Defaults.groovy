package io.gitlab.arturbosch.smartsmells.config

import io.gitlab.arturbosch.smartsmells.smells.middleman.MiddleManVisitor

/**
 * @author artur
 */
class Defaults {

	static int LONG_METHOD = 20
	static int LONG_PARAMETER_LIST = 5
	static int COMPLEX_METHOD = 10
	static int  WEIGHTED_METHOD_COUNT = 20
	static int ACCESS_TO_FOREIGN_DATA = 4
	static double TIED_CLASS_COHESION = 0.33
	static boolean ONLY_PRIVATE_DEAD_CODE = true
	static int LARGE_CLASS = 150
	static int CHAIN_SIZE = 3
	static double FEATURE_ENVY_BASE = 0.5
	static double FEATURE_ENVY_WEIGHT = 0.5
	static double FEATURE_ENVY_FACTOR = 0.52
	static int CHANGING_CLASSES = 5
	static int CHANGING_METHODS = 10
	static int MAX_DEPTH = 3

	static MiddleManVisitor.MMT MIDDLE_MAN_THRESHOLD = MiddleManVisitor.MMT.half
}
