package com.gitlab.artismarti.smartsmells.cycle

import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false, includeNames = false)
class Cycle {

	Dependency source
	Dependency target

}
