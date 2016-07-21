package com.gitlab.artismarti.smartsmells.smells.longparam

import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.smells.longmethod.LongMethod
import groovy.transform.Immutable
import groovy.transform.ToString
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
