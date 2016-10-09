package io.gitlab.arturbosch.smartsmells.smells.longparam

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.smartsmells.common.DetectionResult
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethod

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class LongParameterList implements DetectionResult {

	@Delegate
	LongMethod longMethod
	List<String> parameters
	int numberOfParams
	int numberOfParamsThreshold

	/**
	 * This overrides the getter access to the delegated path property
	 * which is delegated from the delegated long method class which provides no such field exception.
	 *
	 * @return the path from delegated source path
	 */
	String path() {
		sourcePath.path
	}

	@Override
	String asCompactString() {
		"LongParameterList \n\nSize: $numberOfParams with threshold: $numberOfParamsThreshold"
	}
}
