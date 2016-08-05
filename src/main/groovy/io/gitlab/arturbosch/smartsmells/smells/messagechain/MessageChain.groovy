package io.gitlab.arturbosch.smartsmells.smells.messagechain

import groovy.transform.Immutable
import groovy.transform.ToString
import io.gitlab.arturbosch.smartsmells.common.Smelly
import io.gitlab.arturbosch.smartsmells.common.source.SourcePath
import io.gitlab.arturbosch.smartsmells.common.source.SourceRange

/**
 * @author artur
 */
@Immutable
@ToString(includeNames = false, includePackage = false)
class MessageChain implements Smelly {

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
