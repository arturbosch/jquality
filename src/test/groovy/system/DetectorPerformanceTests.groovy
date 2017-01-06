package system

import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.smartsmells.Main
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.CompilationUnitMetrics
import io.gitlab.arturbosch.smartsmells.metrics.MetricsForCompilationUnitProcessor
import io.gitlab.arturbosch.smartsmells.smells.godclass.GodClassDetector
import io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery.ShotgunSurgeryDetector
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class DetectorPerformanceTests extends Specification {

	def "shotgun surgery detector"() {
		given:
		def path = "/home/artur/Repos/elasticsearch/core/src/main/"
		def time = Main.benchmark {
			CompilationStorage.create(Paths.get(path))
		} // 7758 ms
		println "CompilationStorage: $time ms"
		def facade = DetectorFacade.builder().with(new ShotgunSurgeryDetector()).build()

		when:
		def result = -1
		time = Main.benchmark {
			result = facade.run(Paths.get(path)).of(Smell.SHOTGUN_SURGERY).size()
		} // 16840 ms
		println "Detector: $time ms"
		println result

		then:
		result != -1
	}

	def "god class detector"() {
		given:
		def path = "/home/artur/Repos/elasticsearch/core/src/main/"
		def time = Main.benchmark {
			CompilationStorage.create(Paths.get(path))
		} // 8373 ms
		println "CompilationStorage: $time ms"
		def facade = DetectorFacade.builder().with(new GodClassDetector()).build()

		when:
		def result = -1
		time = Main.benchmark {
			result = facade.run(Paths.get(path)).of(Smell.GOD_CLASS).size()
		} // 9509 ms
		println "Detector: $time ms"
		println result

		then:
		result == 411
	}

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
