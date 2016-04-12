package com.gitlab.artismarti.smartsmells.longmethod

import com.gitlab.artismarti.smartsmells.common.SourceRange
import groovy.transform.Immutable

/**
 * @author artur
 */
@Immutable
class LongMethod {

	String header
	String name
	String signature
	int size
	int threshold
	SourceRange sourceRange

}
