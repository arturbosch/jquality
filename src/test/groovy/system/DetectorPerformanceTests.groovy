package system

import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.Main
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade
import io.gitlab.arturbosch.smartsmells.metrics.FileMetricProcessor
import io.gitlab.arturbosch.smartsmells.smells.RefusedParentBequest.RefusedParentBequestDetector
import io.gitlab.arturbosch.smartsmells.smells.brainmethod.BrainMethodDetector
import io.gitlab.arturbosch.smartsmells.smells.classdatashouldbeprivate.ClassDataShouldBePrivateDetector
import io.gitlab.arturbosch.smartsmells.smells.comment.CommentDetector
import io.gitlab.arturbosch.smartsmells.smells.complexmethod.ComplexMethodDetector
import io.gitlab.arturbosch.smartsmells.smells.cycle.CycleDetector
import io.gitlab.arturbosch.smartsmells.smells.dataclass.DataClassDetector
import io.gitlab.arturbosch.smartsmells.smells.deadcode.DeadCodeDetector
import io.gitlab.arturbosch.smartsmells.smells.featureenvy.FeatureEnvyDetector
import io.gitlab.arturbosch.smartsmells.smells.godclass.GodClassDetector
import io.gitlab.arturbosch.smartsmells.smells.largeclass.LargeClassDetector
import io.gitlab.arturbosch.smartsmells.smells.longmethod.LongMethodDetector
import io.gitlab.arturbosch.smartsmells.smells.longparam.LongParameterListDetector
import io.gitlab.arturbosch.smartsmells.smells.messagechain.MessageChainDetector
import io.gitlab.arturbosch.smartsmells.smells.middleman.MiddleManDetector
import io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery.ShotgunSurgeryDetector
import io.gitlab.arturbosch.smartsmells.smells.statechecking.StateCheckingDetector
import io.gitlab.arturbosch.smartsmells.smells.traditionbreaker.TraditionBreakerDetector
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class DetectorPerformanceTests extends Specification {

	private static CompilationStorage storage

	def "shotgun surgery detector"() {
		when: "testing performance"
		run(new ShotgunSurgeryDetector())
		then: "it takes: 7059 ms(, 7523 ms(, 16484 ms))"
		true
	}

	def "cycle detector"() {
		when: "testing performance"
		run(new CycleDetector())
		then: "it takes: 8455 ms"
		true
	}

	def "god class detector"() {
		when: "testing performance"
		run(new GodClassDetector())
		then: "it takes: 6205 ms(, 10445 ms)"
		true
	}

	def "long method detector"() {
		when: "testing performance"
		run(new LongMethodDetector())
		then: "it takes: 7713 ms"
		true
	}

	def "long parameter detector"() {
		when: "testing performance"
		run(new LongParameterListDetector())
		then: "it takes: 8856 ms"
		true
	}

	def "data class detector"() {
		when: "testing performance"
		run(new DataClassDetector())
		then: "it takes: 7208 ms"
		true
	}

	def "dead code detector"() {
		when: "testing performance"
		run(new DeadCodeDetector())
		then: "it takes: 11603 ms(, 12868 ms)"
		true
	}

	def "middle man detector"() {
		when: "testing performance"
		run(new MiddleManDetector())
		then: "it takes: 7205 ms"
		true
	}

	def "message chain detector"() {
		when: "testing performance"
		run(new MessageChainDetector())
		then: "it takes: 5941 ms"
		true
	}

	def "comment detector"() {
		when: "testing performance"
		run(new CommentDetector())
		then: "it takes: 5665 ms"
		true
	}

	def "feature envy detector"() {
		when: "testing performance"
		run(new FeatureEnvyDetector())
		then: "it takes: 17035 ms"
		true
	}

	def "complex method detector"() {
		when: "testing performance"
		run(new ComplexMethodDetector())
		then: "it takes: 6400 ms"
		true
	}

	def "large class detector"() {
		when: "testing performance"
		run(new LargeClassDetector())
		then: "it takes: 6006 ms"
		true
	}

	def "state checking detector"() {
		when: "testing performance"
		run(new StateCheckingDetector())
		then: "it takes: 6006 ms"
		true
	}

	def "class data should be private"() {
		when: "testing performance"
		run(new ClassDataShouldBePrivateDetector())
		then: "it takes: 281 ms"
		true
	}

	def "refused parent bequest detector"() {
		when: "testing performance"
		run(new RefusedParentBequestDetector())
		then: "it takes: 1345 ms"
		true
	}

	def "tradition breaker detector"() {
		when: "testing performance"
		run(new TraditionBreakerDetector())
		then: "it takes: 51 ms"
		true
	}

	def "brain method detector"() {
		when: "testing performance"
		run(new BrainMethodDetector())
		then: "it takes: 347 ms"
		true
	}

	private static CompilationStorage init(Path path) {
		if (storage == null) {
			def time = Main.benchmark {
				storage = JPAL.newInstance(path, new FileMetricProcessor())
			} // 7758 ms / 23458 ms with Metrics
			println "Compilation: $time ms"
		}
		return storage
	}

	private static run(Detector detector) {
		println "Testing ${detector.class.simpleName}"
		def path = Paths.get("/home/artur/master/projects/elasticsearch/core/src/main/java")

		def storage = init(path)
		def facade = DetectorFacade.builder().with(detector).build()

		def result = -1
		def time = Main.benchmark {
			result = facade.run(storage.allCompilationInfo, new Resolver(storage))
					.of(detector.type).size()
		} // 16840 ms
		println "Detector: $time ms"
		println "#smells: $result"
	}

}
