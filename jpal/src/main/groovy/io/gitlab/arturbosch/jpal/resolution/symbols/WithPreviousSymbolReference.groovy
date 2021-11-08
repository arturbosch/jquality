package io.gitlab.arturbosch.jpal.resolution.symbols

import groovy.transform.PackageScope
import io.gitlab.arturbosch.jpal.resolution.QualifiedType

/**
 * @author Artur Bosch
 */
trait WithPreviousSymbolReference {
	SymbolReference previousReference

	abstract boolean isBuilderPattern()

	@PackageScope
	boolean isBuilderPattern(QualifiedType thisType) {
		def list = new ArrayList<String>()
		list.add(thisType.shortName)
		def previous = previousReference
		while (previous != null) {
			list.add(previous.qualifiedType.shortName)
			previous = (previous as WithPreviousSymbolReference).previousReference
		}
		return list.unique().size() <= 2
	}
}
