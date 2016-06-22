package system

import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.out.XMLWriter
import com.gitlab.artismarti.smartsmells.start.DetectorFacade
import spock.lang.Specification

import java.nio.file.Paths
import java.util.stream.Collectors

/**
 * @author artur
 */
class SystemTestOnQuideIT extends Specification {

	def "metrics on quide"() {
		given:
		def path = "/home/artur/Repos/quide/Implementierung/QuideService/src/main"

		when:
		def result = DetectorFacade.metricFacade().run(Paths.get(path)).of(Smell.CLASS_INFO)
		result.each { println(it.toString()) }
		println "size: ${result.size()}"

		then:
		result.size() > 0
	}

	def "run on quide and find no same feature envy twice"() {
		given:
//		def path = "/home/artur/Repos/quide/Implementierung/QuideService/src/main"
//		def path = "/home/artur/Arbeit/tools/ismell/src/main"
//		def path = "/home/artur/Arbeit/pooka-co/trunk/pooka/src"
		def path = Paths.getResource("/cornercases").getFile()
		def result = DetectorFacade.builder().fullStackFacade().run(Paths.get(path))
		def envies = result.of(Smell.FEATURE_ENVY)
		when:
		def duplicates = envies.stream()
				.filter { Collections.frequency(envies, it) > 1 }
				.collect(Collectors.toSet())
//		envies.each { println it.toString() }
//		result.of(Smell.GOD_CLASS).each { println it.toString() }
		println result.of(Smell.GOD_CLASS).size()
		then:
		duplicates.isEmpty()
		when:
		def xml = XMLWriter.toXml(result)
		println xml
		then:
		xml.contains("FeatureEnvy")
	}
}
