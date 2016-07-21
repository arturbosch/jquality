package com.gitlab.artismarti.smartsmells.smells.cycle

import com.gitlab.artismarti.smartsmells.common.Smelly
import groovy.transform.ToString

/**
 * @author artur
 */
@ToString(includeNames = false, includePackage = false)
class Cycle implements Smelly {

	Dependency source
	Dependency target

	Cycle(Dependency source, Dependency target) {
		this.source = source
		this.target = target
	}

	@Override
	String asCompactString() {
		"Cycle \n\nSource: $source.name\nTarget: $target.name"
	}

	@Override
	boolean equals(Object obj) {
		if (obj instanceof Cycle) {
			return (source.equals(obj.source) && target.equals(obj.target)) ||
					(source.equals(obj.target) && target.equals(obj.source))
		}
		return false
	}

	@Override
	public String toString() {
		return "Cycle{" +
				"source=" + source.toString() +
				", target=" + target.toString() +
				'}';
	}

	@Override
	int hashCode() {
		return Objects.hash(source) + Objects.hash(target)
	}
}
