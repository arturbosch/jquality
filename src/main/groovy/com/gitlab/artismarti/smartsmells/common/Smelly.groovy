package com.gitlab.artismarti.smartsmells.common

import com.gitlab.artismarti.smartsmells.common.source.SourceRange

/**
 * @author artur
 */
trait Smelly {

	SourceRange getPositions() {
		return getAttribute(this, "sourceRange") as SourceRange
	}

	String getPathAsString() {
		return getAttribute(this, "sourcePath").toString()
	}

	private static Object getAttribute(Smelly smelly, String name) {
		return smelly.class.getDeclaredField(name).with {
			setAccessible(true)
			get(smelly)
		}
	}
}
