package io.gitlab.arturbosch.smartsmells.metrics.raisers

import io.gitlab.arturbosch.smartsmells.common.Test
import io.gitlab.arturbosch.smartsmells.metrics.FileInfo

/**
 * @author Artur Bosch
 */
class MAXNESTINGTest extends MetricSpecification {

	def "find max nesting of 3"() {
		given:
		def code = """
			class A {
				void m() {
					if (true) {
						while (true) {
						
						}
					}
					
					if (true) {
						System.out.println();
					} else {
						if (true) {
							while (true) {
							
							}
						}
					}
					if (true) {
						while (true) {
						}
					}
				}
			}
		"""
		def info = setupCode(code)[0]
		def methodInfo = info.getData(FileInfo.KEY)?.findClassByName("A")?.getMethodByName("m")

		when:
		new MAXNESTING().raise(Test.nth(info.unit, 0), methodInfo, resolver)

		then:
		methodInfo.getMetric(MAXNESTING.MAXNESTING).value == 3
	}

}
