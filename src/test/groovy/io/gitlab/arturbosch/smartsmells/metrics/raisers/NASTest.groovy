package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.jpal.core.CompilationInfo
import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Test
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo
import io.gitlab.arturbosch.smartsmells.metrics.FileInfoAppender
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class NASTest extends Specification {

	Resolver resolver
	CompilationInfo cinfo

	def setup() {
		def base = Test.BASE_PATH.resolve("bla")
		resolver = new Resolver(JPAL.newInstance(base, new FileInfoAppender()))
		cinfo = resolver.find(base.resolve("SelfEnvy.java")).get()
	}

	def "test"() {
		given:
		def nas = new NAS()
		when:
		nas.raise(cinfo.mainType as ClassOrInterfaceDeclaration, cinfo, resolver)
		def nasMetric = cinfo.getData(FileInfo.KEY).findClassByName("SelfEnvy")
				.getMetric(NAS.NUMBER_OF_ADDED_SERVICES)
		def pnasMetric = cinfo.getData(FileInfo.KEY).findClassByName("SelfEnvy")
				.getMetric(NAS.PERCENTAGE_OF_NEWLY_ADDED_SERVICES)
		then:
		nasMetric.value == 1
		pnasMetric.asDouble() == 0.14d
	}
}
