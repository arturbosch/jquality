package io.gitlab.arturbosch.smartsmells.api

import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Test
import io.gitlab.arturbosch.smartsmells.metrics.ClassInfoDetector
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileMetricProcessor
import io.gitlab.arturbosch.smartsmells.metrics.internal.FullstackMetrics
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class SmartSmellsTest extends Specification {

	def "test"() {
		given: "SmartSmells instance"
		def classInfoDetector = new ClassInfoDetector(FullstackMetrics.create())
		def metricProcessor = new FileMetricProcessor(classInfoDetector)
		def storage = JPAL.updatable(metricProcessor)
		def resolver = new Resolver(storage)
		def smartSmells = new SmartSmells(storage, resolver, DetectorFacade.fullStackFacade())
		def testPaths = [Test.COMPLEX_METHOD_DUMMY_PATH, Test.COMPLEX_CONDITION_DUMMY_PATH]

		when:
		smartSmells.addOrUpdate(testPaths)
		smartSmells.executeOnUpdated()
		def fileInfos = storage.allCompilationInfo.collect { it.getProcessedObject(FileInfo.class) }

		fileInfos.each { println(it) }

		then:
		fileInfos.size() == testPaths.size()
	}
}
