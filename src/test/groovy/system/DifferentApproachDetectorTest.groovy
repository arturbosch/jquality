package system

import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.Main
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade
import io.gitlab.arturbosch.smartsmells.config.Smell
import io.gitlab.arturbosch.smartsmells.metrics.CompilationUnitMetrics
import io.gitlab.arturbosch.smartsmells.metrics.MetricsForCompilationUnitProcessor
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class DifferentApproachDetectorTest extends Specification {

	@SuppressWarnings("GroovyVariableNotAssigned")
	"god class with processor"() {
		given:
		def path = "/home/artur/Repos/elasticsearch/core/src/main/"
		CompilationStorage storage
		def time = Main.benchmark {
			storage = JPAL.new(Paths.get(path), new MetricsForCompilationUnitProcessor())
		}
		def infos = storage.allCompilationInfo
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
			result = facade.justRun(infos, new Resolver(storage)).of(Smell.GOD_CLASS).size()
		}
		println "Detector: $time ms"
		println result // should be 411 ... , but why?

		then:
		result != -1
	}

}
