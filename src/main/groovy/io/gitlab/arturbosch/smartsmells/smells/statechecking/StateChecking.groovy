package io.gitlab.arturbosch.smartsmells.smells.statechecking

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult

/**
 * @author Artur Bosch
 */
@Immutable
@ToString(includePackage = false)
class StateChecking implements DetectionResult {

	static String INSTANCE_OF = "Replace instanceof conditions with polymorphism"
	static String SUBTYPING = "Replace state checking variables with subtyping"

	String inScope //ClassSignature#MethodSignature
	List<String> cases = new ArrayList<>()
	String type

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	String signature() {
		return "$inScope#${cases.join(", ")}"
	}

	@Override
	String asCompactString() {
		return "StateChecking \n\n$type"
	}

}
