package com.gitlab.artismarti.smartsmells.messagechain

import com.gitlab.artismarti.smartsmells.common.Defaults
import com.gitlab.artismarti.smartsmells.common.Detector
import com.gitlab.artismarti.smartsmells.common.Smell
import com.gitlab.artismarti.smartsmells.common.Visitor

import java.nio.file.Path

/**
 * @author artur
 */
class MessageChainDetector extends Detector<MessageChain> {

	private int chainSizeThreshold

	MessageChainDetector(int chainSizeThreshold = Defaults.CHAIN_SIZE) {
		this.chainSizeThreshold = chainSizeThreshold
	}

	@Override
	protected Visitor getVisitor(Path path) {
		return new MessageChainVisitor(path, chainSizeThreshold)
	}

	@Override
	Smell getType() {
		return Smell.MESSAGE_CHAIN
	}
}
