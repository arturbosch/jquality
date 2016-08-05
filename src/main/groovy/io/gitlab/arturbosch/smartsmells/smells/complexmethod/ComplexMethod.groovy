package io.gitlab.arturbosch.smartsmells.smells.complexmethod

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.smartsmells.common.Smelly
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethod

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class ComplexMethod implements Smelly {

	@Delegate
	LongMethod longMethod

	int cyclomaticComplexity

	@Override
	String asCompactString() {
		"ComplexMethod \n\nCyclomaticComplexity: $cyclomaticComplexity"
	}
}
