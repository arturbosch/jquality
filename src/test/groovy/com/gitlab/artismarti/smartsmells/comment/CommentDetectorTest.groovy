package com.gitlab.artismarti.smartsmells.comment

import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author artur
 */
class CommentDetectorTest extends Specification {

	void "three comments detected"() {
		expect:
		smells.size() == 3

		where:
		smells = new CommentDetector()
				.run(Paths.get("./src/test/groovy"))
	}
}
