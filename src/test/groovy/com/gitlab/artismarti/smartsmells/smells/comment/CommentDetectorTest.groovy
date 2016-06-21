package com.gitlab.artismarti.smartsmells.smells.comment

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class CommentDetectorTest extends Specification {

	void "three comments detected"() {
		expect:
		smells.size() == 3
		!smells.getAt(0).type.isEmpty()
		!smells.getAt(0).message.isEmpty()
		!smells.getAt(0).path.isEmpty()
		smells.getAt(0).sourceRange != null
		smells.getAt(0).sourcePath != null

		where:
		smells = new CommentDetector().run(Test.PATH)
	}
}
