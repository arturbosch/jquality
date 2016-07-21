package com.gitlab.artismarti.smartsmells.common

import com.gitlab.artismarti.smartsmells.api.SmellExchange
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import com.gitlab.artismarti.smartsmells.smells.complexmethod.ComplexMethod
import com.gitlab.artismarti.smartsmells.smells.longparam.LongParameterList

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
		switch (smelly) {
			case ComplexMethod:
			case LongParameterList: return SmellExchange.getAttribute(
					SmellExchange.getAttribute(smelly, "longMethod") as Smelly, name)
			default: return SmellExchange.getAttribute(smelly, name)
		}
	}

}
