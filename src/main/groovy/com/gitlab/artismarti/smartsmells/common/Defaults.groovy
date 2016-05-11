package com.gitlab.artismarti.smartsmells.common

import com.gitlab.artismarti.smartsmells.middleman.MiddleManVisitor

/**
 * @author artur
 */
class Defaults {

	static int LONG_METHOD = 7
	static int LONG_PARAMETER_LIST = 5
	static int COMPLEX_METHOD = 10
	static int  WEIGHTED_METHOD_COUNT = 20
	static int ACCESS_TO_FOREIGN_DATA = 4
	static double TIED_CLASS_COHESION = 0.33
	static boolean ONLY_PRIVATE_DEAD_CODE = true
	static int LARGE_CLASS = 150
	static int CHAIN_SIZE = 2
	static double FEATURE_ENVY_FACTOR = 0.52
	static MiddleManVisitor.MMT MIDDLE_MAN_THRESHOLD = MiddleManVisitor.MMT.half

}
