package io.gitlab.arturbosch.smartsmells.smells.comment

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class CommentDetectorTest extends Specification {

	def "three comments within private methods detected"() {
		expect:
		smells.size() == 3
		smells.each { it.type == CommentSmell.Type.ORPHAN || it.type == CommentSmell.Type.PRIVATE }

		where:
		smells = new CommentDetector().run(Test.COMMENT_DUMMY_PATH)
	}
}
