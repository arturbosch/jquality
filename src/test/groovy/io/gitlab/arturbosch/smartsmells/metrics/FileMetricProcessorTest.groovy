package io.gitlab.arturbosch.smartsmells.metrics

import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class FileMetricProcessorTest extends Specification {

	@Ignore
	def "metrics are collected for dummies"() {
		given: "compilation storage created with a metric processor"
		def dummy = Test.DATA_CLASS_DUMMY_PATH
		def storage = JPAL.newInstance(dummy, new FileMetricProcessor())
		when: "looking at the compilation units"
		def cis = storage.getCompilationInfo(dummy).get()
		then: "all must have a metrics object"
		cis.getData(FileInfo.KEY).classes[0].name == "DataClassDummy"
	}

}
