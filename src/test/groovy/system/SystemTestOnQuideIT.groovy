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

	def "run on quide and find no same feature envy twice"() {
		given:
//		def path = "/home/artur/Repos/quide/Implementierung/QuideService/"
//		def path = "/home/artur/Repos/quide/Implementierung/Analyse/in/679a88fdae553bbab0c841bb0df66e294e90ffaa/rxjava-core/src/main/java/rx/operators/OperationAverage.java"
//		def path = "/home/artur/Arbeit/pooka-co/trunk/pooka/src"
		def path = Paths.getResource("/cornercases").getFile()
		def result = DetectorFacade.builder().fullStackFacade().run(Paths.get(path))
		def envies = result.of(Smell.FEATURE_ENVY)
		when:
		def duplicates = envies.stream()
				.filter { Collections.frequency(envies, it) > 1 }
				.collect(Collectors.toSet())
//		envies.each { println it.toString() }
		then:
		duplicates.isEmpty()
		when:
		def xml = XMLWriter.toXml(result)
		println xml
		then:
		xml.contains("FeatureEnvy")
	}
}
