package com.gitlab.artismarti.smartsmells.integration

import com.gitlab.artismarti.smartsmells.common.Test
import com.gitlab.artismarti.smartsmells.config.DetectorConfig
import com.gitlab.artismarti.smartsmells.out.XMLBuilder
import com.gitlab.artismarti.smartsmells.start.DetectorFacade
import spock.lang.Specification

import java.nio.file.Paths
/**
 * @author artur
 */
class ConfigurationIT extends Specification {

	def "create detector facade from config und run over test dummies"() {
		expect:
		result.smellSets.size() == 12

		where:
		path = Paths.get(getClass().getResource("/integration.yml").getFile())
		facade = DetectorFacade.fromConfig(DetectorConfig.load(path))
		result = facade.run(Test.PATH)
	}

	def "create detector facade as full stack facade and run over test dummies"() {
		when:
		def result = DetectorFacade.builder().fullStackFacade().run(Test.PATH)
		then:
		result.smellSets.size() == 12
		XMLBuilder.toXml(result)
	}
}
