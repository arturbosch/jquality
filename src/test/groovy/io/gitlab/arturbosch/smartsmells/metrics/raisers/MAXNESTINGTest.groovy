package io.gitlab.arturbosch.smartsmells.metrics.raisers

import com.github.javaparser.JavaParser
import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class MAXNESTINGTest extends Specification {

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

		def unit = JavaParser.parse(code)

		when:
		def nesting = new MAXNESTING().raise(Test.nth(unit, 0), null)

		then:
		nesting.value == 3
	}
}
