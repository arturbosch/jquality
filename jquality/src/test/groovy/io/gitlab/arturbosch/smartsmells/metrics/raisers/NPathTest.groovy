package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.MethodDeclaration
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Test
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileInfoAppender
import io.gitlab.arturbosch.smartsmells.metrics.Metric
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class NPathTest extends Specification {

	Resolver resolver
	CompilationInfo cinfo

	def setup() {
		def path = Test.NPATH_DUMMY
		resolver = new Resolver(JPAL.newInstance(path, new FileInfoAppender()))
		cinfo = resolver.find(path).get()
	}

	private Metric raiseMetricForMethodWithName(String name) {
		def npath = new NPATH()
		def method = cinfo.mainType.getChildNodesByType(MethodDeclaration.class)[0]
		def methodInfo = cinfo.getData(FileInfo.KEY).findClassByName("NPathDummy")
				.getMethodByName(name)

		npath.raise(method, methodInfo, resolver)
		methodInfo.getMetric(NPATH.NPATH)
	}

	def "npath of m1"() {
		when:
		def npathMetric = raiseMetricForMethodWithName("m1")

		then:
		npathMetric.value == 3
	}

	def "npath of m2"() {
		when:
		def npathMetric = raiseMetricForMethodWithName("m2")

		then:
		npathMetric.value == 6
	}
}
