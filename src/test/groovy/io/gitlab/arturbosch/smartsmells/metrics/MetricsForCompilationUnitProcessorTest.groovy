package io.gitlab.arturbosch.smartsmells.metrics

import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class MetricsForCompilationUnitProcessorTest extends Specification {

	def "metrics are collected for dummies"() {
		given: "compilation storage created with a metric processor"
		def dummy = Test.DATA_CLASS_DUMMY_PATH
		CompilationStorage.createWithProcessor(dummy, new MetricsForCompilationUnitProcessor())
		when: "looking at the compilation units"
		def cis = CompilationStorage.getCompilationInfo(dummy).get()
		then: "all must have a metrics object"
		cis.getProcessedObject(CompilationUnitMetrics.class).infos[0].name == "DataClassDummy"
	}

}
