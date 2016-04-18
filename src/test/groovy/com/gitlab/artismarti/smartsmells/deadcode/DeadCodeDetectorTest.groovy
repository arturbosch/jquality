package com.gitlab.artismarti.smartsmells.deadcode

import com.gitlab.artismarti.smartsmells.common.Test
import org.junit.Ignore
import spock.lang.Specification

/**
 * @author artur
 */
@Ignore
class DeadCodeDetectorTest extends Specification {

	def "find one unused field, method, parameter and locale variable"() {
		expect:
		smells.size() == 4

		where:
		smells = new DeadCodeDetector().run(Test.DEAD_CODE_PATH)
	}
}
