package io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.DetectionResult

/**
 * @author Artur Bosch
 */
@Immutable
@ToString(includePackage = false, excludes = ["ccThreshold", "cmThreshold"])
class ShotgunSurgery implements DetectionResult {
	String name
	String signature
	int cc
	int cm
	int ccThreshold
	int cmThreshold
	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	@Override
	String asCompactString() {
		"ShotgunSurgery \n\nCC=$cc with threshold: $ccThreshold" +
				"\nCM=$cm with threshold: $cmThreshold"
	}

}
