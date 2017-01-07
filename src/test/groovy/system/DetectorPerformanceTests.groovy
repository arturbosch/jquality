package system

import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.smartsmells.Main
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade
import io.gitlab.arturbosch.smartsmells.common.Detector
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.CompilationUnitMetrics
import io.gitlab.arturbosch.smartsmells.metrics.MetricsForCompilationUnitProcessor
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
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class DetectorPerformanceTests extends Specification {


	def "shotgun surgery detector"() {
		when: "testing performance"
		run(new ShotgunSurgeryDetector())
		then: "it takes: 16484 ms"
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
		then: "it takes: 10445 ms"
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
		then: "it takes: 12868 ms"
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

	private static init(Path path) {
		if (!CompilationStorage.initialized) {
			def time = Main.benchmark {
				CompilationStorage.create(path)
			} // 7758 ms
			println "Compilation: $time ms"
		}
	}

	private static run(Detector detector) {
		println "Testing ${detector.class.simpleName}"
		def path = Paths.get("/home/artur/Repos/elasticsearch/core/src/main/")

		init(path)
		def facade = DetectorFacade.builder().with(detector).build()

		def result = -1
		def time = Main.benchmark {
			result = facade.run(path).of(detector.type).size()
		} // 16840 ms
		println "Detector: $time ms"
		println result
	}

	@Ignore
	def "all detectors"() {
		given:
		def path = "/home/artur/Repos/elasticsearch/core/src/main/"
		def time = Main.benchmark {
			CompilationStorage.create(Paths.get(path))
		} // ~7500 to ~8500 ms
		println "CompilationStorage: $time ms"
		def facade = DetectorFacade.fullStackFacade()

		when:
		def result = -1
		time = Main.benchmark {
			result = facade.run(Paths.get(path)).smellSets.size()
		} //  27574 (w/o SS) ms, 35351 (w/ SS) ms
		println "Detectors: $time ms"
		println result

		then:
		result != -1
	}

	def "god class with processor"() {
		given:
		def path = "/home/artur/Repos/elasticsearch/core/src/main/"
		def time = Main.benchmark {
			CompilationStorage.createWithProcessor(Paths.get(path), new MetricsForCompilationUnitProcessor())
		}
		def infos = CompilationStorage.allCompilationInfo
		def count = infos.stream().map { it.getProcessedObject(CompilationUnitMetrics.class) }.map {
			it.infos
		}.flatMap { it.stream() }
				.count()
		println "Infos: $count"
		println "CompilationStorage: $time ms"
		def facade = DetectorFacade.builder().with(new GodClassMetricVisitor()).build()

		when:
		def result = -1
		time = Main.benchmark {
			result = facade.justRun(infos).of(Smell.GOD_CLASS).size()
		}
		println "Detector: $time ms"
		println result // should be 411 ... , but why?

		then:
		result != -1
	}

}
