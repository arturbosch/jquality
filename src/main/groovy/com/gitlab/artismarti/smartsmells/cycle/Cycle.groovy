package com.gitlab.artismarti.smartsmells.cycle
/**
 * @author artur
 */
class Cycle {

	Dependency source
	Dependency target

	Cycle(Dependency source, Dependency target) {
		this.source = source
		this.target = target
	}

	@Override
	boolean equals(Object obj) {
		if (obj instanceof Cycle) {
			if (source.equals(obj.source) && target.equals(obj.target)) {
				return true
			}
			if (source.equals(obj.target) && target.equals(obj.source)) {
				return true
			}
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
}
