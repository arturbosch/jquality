package com.gitlab.artismarti.smartsmells.messagechain

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class MessageChainDetectorTest extends Specification {

	def "find one message chains with size 2"() {
		expect:
		smells.size() == 1
		smells.getAt(0).chainSize == 2
		smells.getAt(0).chainSizeThreshold == 2
		smells.getAt(0).sourceEntity == "chainMiddle"
		smells.getAt(0).targetEntity == "complexComputation"
		smells.getAt(0).sourcePath != null
		smells.getAt(0).sourceRange != null

		where:
		smells = new MessageChainDetector().run(Test.MESSAGE_CHAIN_PATH)
	}
}
