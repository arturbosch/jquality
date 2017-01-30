package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.DetectionResult
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.smells.comment.CommentSmell
import io.gitlab.arturbosch.smartsmells.smells.complexmethod.ComplexMethod
import io.gitlab.arturbosch.smartsmells.smells.cycle.Cycle
import io.gitlab.arturbosch.smartsmells.smells.cycle.Dependency
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethod
import spock.lang.Specification

/**
 * @author artur
 */
class SmellResultTest extends Specification {

	def testFilter() {
		given: "smell result with three comment smells"
		Deque deque = new ArrayDeque<DetectionResult>()
		deque.addAll(getComplexMethod(), getCycle(), getComplexMethod())
		def map = new HashMap()
		map.put(Smell.COMMENT, deque)
		def smells = new SmellResult(map)

		when: "filtering for path"
		def filtered = smells.filter("path")

		then: "all smells are found where SourcePath == path"
		filtered.size() == 3
	}

	private static CommentSmell getComplexMethod() {
		new CommentSmell(CommentSmell.Type.PRIVATE, "message", false, false, new SourcePath("path"), SourceRange.of(1, 1, 1, 1))
	}

	private static Cycle getCycle() {
		new Cycle(new Dependency("1", "1", new SourcePath("path"), SourceRange.of(1, 1, 1, 1)),
				new Dependency("2", "2", new SourcePath("path"), SourceRange.of(2, 2, 2, 2)))
	}

	def "test reflection methods on smelly objects"() {
		when:
		def smell = getComplexMethod()
		def complexMethod = new ComplexMethod(new LongMethod("name", "signature", 5, 5,
				SourceRange.of(1, 1, 1, 1), new SourcePath("path")), 5)

		then:
		smell.positions.toString() == "SourceRange(1, 1, 1, 1)"
		smell.pathAsString == "path"
		complexMethod.pathAsString == "path"
		complexMethod.positions.toString() == "SourceRange(1, 1, 1, 1)"

	}
}
