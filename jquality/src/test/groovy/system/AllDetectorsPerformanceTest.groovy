package system

import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.Main
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade
import io.gitlab.arturbosch.smartsmells.metrics.FileMetricProcessor
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class AllDetectorsPerformanceTest extends Specification {

	def "all detectors"() {
		given:
		def path = Paths.get("/home/artur/master/projects/elasticsearch/core/src/main/java")
		CompilationStorage storage
		def time = Main.benchmark {
			storage = JPAL.newInstance(path, new FileMetricProcessor())
		} // ~7500 to ~8500 ms
		// JPAL RC5: ~9600 ms
		// Metric Preprocessing: 21823
		println "CompilationStorage: $time ms"
		def facade = DetectorFacade.builder().fullStackFacade()

		when:
		def detectors = -1
		def smells = -1
		time = Main.benchmark {
			def smellSets = facade.run(storage.allCompilationInfo, new Resolver(storage)).smellSets
			detectors = smellSets.size()
			smells = smellSets.values().flatten().size()
		} //  27574 (w/o SS) ms, 35351 (w/ SS) ms
		// JPAL RC2: 23k-24456 ms, 33k ms
		// JPAL RC5: 34k ms
		// Metric Preprocessing: 14110
		println "Detectors: $time ms"
		println "#detectors: $detectors"
		println "#smells: $smells"

		then:
		detectors != -1
	}

}
