package com.gitlab.artismarti.smartsmells.integration

import com.gitlab.artismarti.smartsmells.common.Test
import com.gitlab.artismarti.smartsmells.config.DetectorConfig
import com.gitlab.artismarti.smartsmells.out.XMLWriter
import com.gitlab.artismarti.smartsmells.start.DetectorFacade
import spock.lang.Specification

import java.nio.file.Paths

/**
 * @author artur
 */
class ConfigurationIT extends Specification {

	def "create detector facade from config und run over test dummies"() {

		when:
		def path = Paths.get(getClass().getResource("/integration.yml").getFile())
		def facade = DetectorFacade.fromConfig(DetectorConfig.load(path))
		def result = facade.run(Test.PATH)

		then:
		result.smellSets.size() == 12

		when:
		def xml = XMLWriter.toXml(result)

		then:
		println xml
		xml.startsWith("<SmartSmells>")
		xml.endsWith("</SmartSmells>")
	}

	def "create detector facade as full stack facade and run over test dummies"() {

		when:
		def result = DetectorFacade.fullStackFacade().run(Test.PATH)

		then:
		result.smellSets.size() == 12

		when:
		def xml = XMLWriter.toXml(result)

		then:
		xml.startsWith("<SmartSmells>")
		xml.endsWith("</SmartSmells>")
	}


	def "create metrics facade"() {
		when:
		def result = DetectorFacade.metricFacade().run(Test.PATH)

		then:
		result.smellSets.size() == 1

		when:
		def xml = XMLWriter.toXml(result)
		println xml

		then:
		xml.contains("ClassInfo")
	}
}
