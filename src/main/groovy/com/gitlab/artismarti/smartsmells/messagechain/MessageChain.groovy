package com.gitlab.artismarti.smartsmells.messagechain

import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import groovy.transform.Immutable
import groovy.transform.ToString

/**
 * @author artur
 */
@Immutable
@ToString(includeNames = false, includePackage = false)
class MessageChain {

	String signature
	String sourceEntity
	String targetEntity
	int chainSize
	int chainSizeThreshold

	@Delegate
	SourcePath sourcePath
	@Delegate
	SourceRange sourceRange
}
