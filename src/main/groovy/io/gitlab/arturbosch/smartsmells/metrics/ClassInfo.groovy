package io.gitlab.arturbosch.smartsmells.metrics

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult

/**
 * @author artur
 */
@Immutable
@ToString(includePackage = false, includeNames = true)
class ClassInfo implements DetectionResult {
	String name
	int wmc
	double tcc
	int atfd
	int noa
	int nom
	int loc
	int sloc
	double mlm
	double plm
	double mld
	double pld
	int cc
	int cm
	String signature
	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	@Override
	String asCompactString() {
		return toString()
	}

}
