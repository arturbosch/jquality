package io.gitlab.arturbosch.smartsmells.metrics.internal

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Test
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileMetricProcessor
import io.gitlab.arturbosch.smartsmells.metrics.raisers.LOC
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class LinesOfCodeTest extends Specification {

	def "loc values for comment dummy"() {
		given: "compilation storage created with a metric processor"
		def dummy = Test.COMMENT_DUMMY_PATH
		def storage = JPAL.newInstance(dummy, new FileMetricProcessor())
		def cis = storage.getCompilationInfo(dummy).get()
		def classInfo = cis.getData(FileInfo.KEY).classes[0]

		when:
		new LOC().raise(cis.mainType as ClassOrInterfaceDeclaration, cis, new Resolver(storage))

		then:
		classInfo.metrics.find { it.type == LOC.SLOC }.value == 10
		classInfo.metrics.find { it.type == LOC.CLOC }.value == 13
		classInfo.metrics.find { it.type == LOC.BLOC }.value == 2
		classInfo.metrics.find { it.type == LOC.LLOC }.value == 7
		classInfo.metrics.find { it.type == LOC.LOC }.value == 25
	}
}
