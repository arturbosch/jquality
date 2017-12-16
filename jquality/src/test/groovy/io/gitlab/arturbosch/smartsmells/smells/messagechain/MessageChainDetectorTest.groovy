package io.gitlab.arturbosch.smartsmells.smells.messagechain

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class MessageChainDetectorTest extends Specification {

	def "find one message chains with size 2"() {
		expect:
		smells.size() == 1
		smells[0].chainSize == 3
		smells[0].chainSizeThreshold == 3
		smells[0].sourceEntity == "chainMiddle"
		smells[0].targetEntity == "complexComputation"
		smells[0].sourcePath != null
		smells[0].sourceRange != null

		where:
		smells = new MessageChainDetector().run(Test.MESSAGE_CHAIN_PATH)
	}
}
