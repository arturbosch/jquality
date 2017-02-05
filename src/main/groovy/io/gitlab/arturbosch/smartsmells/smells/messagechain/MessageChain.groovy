package io.gitlab.arturbosch.smartsmells.smells.messagechain

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.smells.DetectionResult

/**
 * @author artur
 */
@Immutable
@ToString(includeNames = false, includePackage = false)
class MessageChain implements DetectionResult {

	String signature
	String sourceEntity
	String targetEntity
	int chainSize
	int chainSizeThreshold

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange

	@Override
	String asCompactString() {
		"MessageChain\n\nchain size: $chainSize with threshold " +
				"$chainSizeThreshold\nSource: $sourceEntity\nTarget: $targetEntity"
	}
}
