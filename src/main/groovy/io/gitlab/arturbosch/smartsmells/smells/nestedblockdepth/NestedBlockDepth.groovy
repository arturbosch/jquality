package io.gitlab.arturbosch.smartsmells.smells.nestedblockdepth

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.smartsmells.common.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethod

/**
 * @author Artur Bosch
 */
@Immutable
@ToString(includePackage = false)
class NestedBlockDepth implements DetectionResult {

	@Delegate
	LongMethod longMethod
	int depth
	int depthThreshold

	@Override
	String asCompactString() {
		return null
	}
}
