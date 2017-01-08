package io.gitlab.arturbosch.smartsmells.smells.cycle

import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.smartsmells.common.Test

import java.nio.file.Paths

/**
 * Strangely tests using CompilationTree as compilation unit storage fail sometimes (after some months)
 * when running them outside of the idea.
 *
 * @author artur
 */
class CycleDetectorTest extends AbstractCompilationTreeTest {

	def "find one cycle in CycleDummy and OtherCycle, one as inner classes of CycleDummy"() {
		given:
		CompilationStorage.create(Test.PATH)
		when:
		def smells = new CycleDetector().run(Test.CYCLE_DUMMY_PATH)
		then:
		smells.size() == 2
	}

	def "find cycle in inner classes of CycleDummy"() {
		given:
		CompilationStorage.create(Test.CYCLE_DUMMY_PATH)
		when:
		def smells = new CycleDetector().run(Test.CYCLE_DUMMY_PATH)
		then:
		smells.size() == 1
	}

	def "cycles are equals, dependency position doesn't matter"() {
		expect:
		cycle == cycle2

		where:
		dep1 = Dependency.of("me", "me", SourcePath.of(Paths.get("me")), SourceRange.of(1, 1, 1, 1))
		dep2 = Dependency.of("you", "you", SourcePath.of(Paths.get("you")), SourceRange.of(2, 2, 2, 2))
		cycle = new Cycle(dep1, dep2)
		cycle2 = new Cycle(dep2, dep1)

	}
}
