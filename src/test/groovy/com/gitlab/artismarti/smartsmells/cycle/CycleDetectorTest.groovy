package com.gitlab.artismarti.smartsmells.cycle

import com.gitlab.artismarti.smartsmells.common.Test
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author artur
 */
class CycleDetectorTest extends Specification {

	def "find one cycle"() {
		expect:
		smells.size() == 1

		where:
		smells = new CycleDetector().run(Test.PATH)
	}

	def "cycles are equals, dependency position doesnt matter"() {

		expect:
		cycle == cycle2

		where:
		dep1 = Dependency.of("me", "me", SourcePath.of(Paths.get("me")), SourceRange.of(1, 1, 1, 1))
		dep2 = Dependency.of("you", "you", SourcePath.of(Paths.get("you")), SourceRange.of(2, 2, 2, 2))
		cycle = new Cycle(dep1, dep2)
		cycle2 = new Cycle(dep2, dep1)

	}
}
