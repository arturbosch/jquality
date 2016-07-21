package com.gitlab.artismarti.smartsmells.api

import com.gitlab.artismarti.smartsmells.common.Smelly
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.smells.comment.CommentSmell
import spock.lang.Specification

/**
 * @author artur
 */
class SmellResultTest extends Specification {

	def testFilter() {
		given: "smell result with three comment smells"
		Deque deque = new ArrayDeque<CommentSmell>()
		deque.addAll(getSmell(), getSmell(), getSmell())
		def map = new HashMap()
		map.put(Smell.COMMENT, deque)
		def smells = new SmellResult(map)

		when: "filtering for path"
		def filtered = smells.filter("path")

		then: "all smells are found where SourcePath == path"
		filtered.size() == 3
	}

	private static CommentSmell getSmell() {
		new CommentSmell("type", "message", false, false, new SourcePath("path"), SourceRange.of(1, 1, 1, 1))
	}
}
