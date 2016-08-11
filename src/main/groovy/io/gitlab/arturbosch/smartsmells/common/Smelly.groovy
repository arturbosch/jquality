package io.gitlab.arturbosch.smartsmells.common

import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.api.SmellExchange
import io.gitlab.arturbosch.smartsmells.smells.complexmethod.ComplexMethod
import io.gitlab.arturbosch.smartsmells.smells.cycle.Cycle
import io.gitlab.arturbosch.smartsmells.smells.longparam.LongParameterList

/**
 * @author artur
 */
trait Smelly {

	abstract String asCompactString()

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
			case Cycle: return SmellExchange.getAttribute(
					SmellExchange.getAttribute(smelly, "source") as Smelly, name)
			default: return SmellExchange.getAttribute(smelly, name)
		}
	}

}
