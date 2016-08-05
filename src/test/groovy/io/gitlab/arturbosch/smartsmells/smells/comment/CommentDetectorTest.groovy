package io.gitlab.arturbosch.smartsmells.smells.comment

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class CommentDetectorTest extends Specification {

	void "three comments within private methods detected"() {
		expect:
		smells.size() == 3
		!smells[0].type.isEmpty()
		!smells[0].message.isEmpty()
		!smells[0].path.isEmpty()
		smells[0].sourceRange != null
		smells[0].sourcePath != null

		where:
		smells = new CommentDetector().run(Test.COMMENT_DUMMY_PATH)
	}
}
