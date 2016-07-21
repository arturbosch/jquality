package com.gitlab.artismarti.smartsmells.api

import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.smells.comment.CommentSmell
import com.gitlab.artismarti.smartsmells.smells.complexmethod.ComplexMethod
import com.gitlab.artismarti.smartsmells.smells.longmethod.LongMethod
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

	def "test reflection methods on smelly objects"() {
		when:
		def smell = getSmell()
		def complexMethod = new ComplexMethod(new LongMethod("header", "name", "signature", 5, 5,
				SourceRange.of(1, 1, 1, 1), new SourcePath("path")), 5)

		then:
		smell.positions.toString() == "1,1,1,1"
		smell.pathAsString == "path"
		complexMethod.pathAsString == "path"
		complexMethod.positions.toString() == "1,1,1,1"

	}
}
