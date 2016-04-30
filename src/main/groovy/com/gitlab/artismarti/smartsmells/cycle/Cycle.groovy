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

	@Override
	boolean equals(Object obj) {
		if (this == obj) return true
		if (obj instanceof Cycle) {
			return (source.equals(obj.source) && target.equals(obj.target)) ||
					(source.equals(obj.target) && target.equals(obj.source))
		}
		return false
	}

}
