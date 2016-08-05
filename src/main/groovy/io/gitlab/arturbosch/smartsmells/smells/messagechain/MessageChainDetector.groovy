package io.gitlab.arturbosch.smartsmells.smells.messagechain

import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.common.Visitor
import io.gitlab.arturbosch.smartsmells.config.Defaults
import io.gitlab.arturbosch.smartsmells.config.Smell

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
