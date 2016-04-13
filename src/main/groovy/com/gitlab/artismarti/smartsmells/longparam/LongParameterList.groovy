package com.gitlab.artismarti.smartsmells.longparam

import com.gitlab.artismarti.smartsmells.longmethod.LongMethod
import groovy.transform.Immutable
import groovy.transform.ToString
/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false)
class LongParameterList {

	@Delegate
	LongMethod longMethod
	List<String> parameters

}
