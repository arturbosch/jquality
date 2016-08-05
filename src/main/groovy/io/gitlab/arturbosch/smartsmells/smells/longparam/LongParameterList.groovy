package io.gitlab.arturbosch.smartsmells.smells.longparam

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.smartsmells.common.Smelly
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethod

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class LongParameterList implements Smelly {

	@Delegate
	LongMethod longMethod
	List<String> parameters
	int numberOfParams
	int numberOfParamsThreshold

	@Override
	String asCompactString() {
		"LongParameterList \n\nSize: $numberOfParams with threshold: $numberOfParamsThreshold"
	}
}
