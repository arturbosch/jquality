package com.gitlab.artismarti.smartsmells.comment

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class CommentDetectorTest extends Specification {

	void "three comments detected"() {
		smells.each { println it.toString() }
		expect:
		smells.size() == 3
		!smells.getAt(0).type.isEmpty()
		!smells.getAt(0).comment.isEmpty()
		!smells.getAt(0).message.isEmpty()
		!smells.getAt(0).path.isEmpty()
		smells.getAt(0).sourceRange != null
		smells.getAt(0).sourcePath != null
		smells.getAt(0).hasTODO
		!smells.getAt(0).hasFIXME
		!smells.getAt(1).hasTODO
		!smells.getAt(1).hasFIXME
		smells.getAt(2).hasFIXME

		where:
		smells = new CommentDetector().run(Test.PATH)
	}
}
