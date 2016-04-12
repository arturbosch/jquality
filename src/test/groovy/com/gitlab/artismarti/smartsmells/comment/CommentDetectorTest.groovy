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

		where:
		smells = new CommentDetector()
				.run(Test.PATH)
	}
}
