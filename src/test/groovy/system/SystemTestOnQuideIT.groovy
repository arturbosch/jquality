package system

import com.gitlab.artismarti.smartsmells.common.CompilationStorage
import com.gitlab.artismarti.smartsmells.config.Smell
import com.gitlab.artismarti.smartsmells.metrics.ClassInfo
import com.gitlab.artismarti.smartsmells.metrics.ClassInfoDetector
import com.gitlab.artismarti.smartsmells.out.XMLWriter
import com.gitlab.artismarti.smartsmells.api.DetectorFacade
import spock.lang.Specification

import java.nio.file.Paths
import java.util.stream.Collectors

/**
 * @author artur
 */
class SystemTestOnQuideIT extends Specification {

	def "compilation storage"() {
		given:
//		def path = "/home/artur/Repos/quide/Implementierung/QuideService/src/main"
//		def path = "/home/artur/Repos/quide/Implementierung/QuIDE_Plugin/"
//		def path = "/home/artur/Arbeit/tools/ismell/src/main"
		def path = "/home/artur/Repos/elasticsearch/core/src/main/"
//		def path = "/home/artur/Repos/RxJava/src/main"
//		def path = "/home/artur/Arbeit/pooka-co/trunk/pooka/src"

		when:
		def storage = CompilationStorage.create(Paths.get(path))
		def types = storage.getAllQualifiedTypes()

		then:
		types.each { println it }
		println "size: ${types.size()}"
	}

	def "metrics on quide"() {
		given:
//		def path = "/home/artur/Repos/quide/Implementierung/QuideService/src/main"
//		def path = "/home/artur/Arbeit/tools/ismell/src/main"
		def path = "/home/artur/Repos/elasticsearch/core/src/main/"
//		def path = "/home/artur/Repos/RxJava/src/main"
//		def path = "/home/artur/Arbeit/pooka-co/trunk/pooka/src"

		when:
		def result = DetectorFacade.builder().with(new ClassInfoDetector(true)).build()
				.run(Paths.get(path)).of(Smell.CLASS_INFO)
		result.each { println(it.toString()) }
		println "size: ${result.size()}"

		def locSum = result.stream().mapToInt { (it as ClassInfo).sloc }.sum()
		def locCount = result.stream().mapToInt { (it as ClassInfo).sloc }.count()
		println "project sloc: $locSum"
		println "project sloc per class: ${locSum / locCount}"

		def wmcSum = result.stream().mapToInt { (it as ClassInfo).wmc }.sum()
		def wmcCount = result.stream().mapToInt { (it as ClassInfo).wmc }.count()
		println "project wmc: ${wmcSum / wmcCount}"

		def aftdSum = result.stream().mapToInt { (it as ClassInfo).atfd }.sum()
		def aftdCount = result.stream().mapToInt { (it as ClassInfo).atfd }.count()
		println "project atfd: ${aftdSum / aftdCount}"

		def tccSum = result.stream().mapToDouble() { (it as ClassInfo).tcc }.sum()
		def tccCount = result.stream().mapToDouble { (it as ClassInfo).tcc }.count()
		println "project tcc: ${tccSum / tccCount}"

		def nomSum = result.stream().mapToInt { (it as ClassInfo).nom }.sum()
		def nomCount = result.stream().mapToInt { (it as ClassInfo).nom }.count()
		println "project nom: ${nomSum / nomCount}"

		def noaSum = result.stream().mapToInt { (it as ClassInfo).noa }.sum()
		def noaCount = result.stream().mapToInt { (it as ClassInfo).noa }.count()
		println "project noa: ${noaSum / noaCount}"

		def amlSum = result.stream().mapToDouble() { (it as ClassInfo).mlm }.sum()
		def amlCount = result.stream().mapToDouble() { (it as ClassInfo).mlm }.count()
		println "project mlm: ${amlSum / amlCount}"

		def aplSum = result.stream().mapToDouble { (it as ClassInfo).plm }.sum()
		def aplCount = result.stream().mapToDouble { (it as ClassInfo).plm }.count()
		println "project plm: ${aplSum / aplCount}"

		def smlSum = result.stream().mapToDouble { (it as ClassInfo).mld }.sum()
		def smlCount = result.stream().mapToDouble { (it as ClassInfo).mld }.count()
		println "project mld: ${smlSum / smlCount}"

		def splSum = result.stream().mapToDouble { (it as ClassInfo).pld }.sum()
		def splCount = result.stream().mapToDouble { (it as ClassInfo).pld }.count()
		println "project pld: ${splSum / splCount}"

		then:
		result.size() > 0
	}

	def "run on quide and find no same feature envy twice"() {
		given:
		def path = "/home/artur/Repos/quide/Implementierung/QuideService/src/main"
//		def path = "/home/artur/Arbeit/tools/ismell/src/main"
//		def path = "/home/artur/Arbeit/pooka-co/trunk/pooka/src"
//		def path = "/home/artur/Repos/elasticsearch/core/src/main/"
//		def path = Paths.getResource("/cornercases").getFile()
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
