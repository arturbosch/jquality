package com.gitlab.artismarti.smartsmells.longparam

import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.longmethod.LongMethod
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

	def getPath() {
		longMethod.path
	}
}
