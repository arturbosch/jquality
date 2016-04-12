package com.gitlab.artismarti.smartsmells.comment

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification
/**
 * @author artur
 */
class CommentDetectorTest extends Specification {

	void "three comments detected"() {
		expect:
		smells.size() == 3
		!smells.get(0).type.isEmpty()
		!smells.get(0).comment.isEmpty()
		!smells.get(0).message.isEmpty()
		!smells.get(0).sourcePath.isEmpty()
		!smells.get(0).positions.isEmpty()

		where:
		smells = new CommentDetector()
				.run(Test.PATH)
	}
}
