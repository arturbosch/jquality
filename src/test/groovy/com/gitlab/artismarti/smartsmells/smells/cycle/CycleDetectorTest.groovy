package com.gitlab.artismarti.smartsmells.smells.cycle

import com.gitlab.artismarti.smartsmells.common.CompilationStorage
import com.gitlab.artismarti.smartsmells.common.Test
import com.gitlab.artismarti.smartsmells.common.source.SourcePath
import com.gitlab.artismarti.smartsmells.common.source.SourceRange

import java.nio.file.Paths

/**
 * @author artur
 */
class CycleDetectorTest extends AbstractCompilationTreeTest {

	def "find one cycle in CycleDummy and OtherCycle, one as inner classes of CycleDummy"() {
		expect:
		smells.size() == 2

		where:
		smells = new CycleDetector(Test.PATH).run(Test.CYCLE_DUMMY_PATH)
	}

	def "cycles are equals, dependency position doesn't matter"() {
		given:
		CompilationStorage.create(Test.PATH)
		expect:
		cycle == cycle2

		where:
		dep1 = Dependency.of("me", "me", SourcePath.of(Paths.get("me")), SourceRange.of(1, 1, 1, 1))
		dep2 = Dependency.of("you", "you", SourcePath.of(Paths.get("you")), SourceRange.of(2, 2, 2, 2))
		cycle = new Cycle(dep1, dep2)
		cycle2 = new Cycle(dep2, dep1)

	}
}
