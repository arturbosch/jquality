package io.gitlab.arturbosch.smartsmells.smells.RefusedParentBequest

import io.gitlab.arturbosch.smartsmells.DetectorSpecification
import io.gitlab.arturbosch.smartsmells.api.Detector
import org.junit.Ignore

/**
 * @author Artur Bosch
 */
class RefusedParentBequestVisitorTest extends DetectorSpecification<RefusedParentBequest> {

	@Override
	Detector<RefusedParentBequest> detector() {
		return new RefusedParentBequestDetector()
	}

	@spock.lang.Ignore
	def "find one"() {
		given:
		def code = """
			public class A extends B {
				int i1 = 5;
				int i2 = 5;
				int i3 = 5;
				int i4 = 5;
				int i5 = 5;
				int i6 = 5;
				int i7 = 5;
				int i8 = 5;
				@Override
				public void m() {
					while(true) {
						if(true){
							if(true) {
							}
						}
					}
				}
			}
		"""
		def code2 = """
			class B {
				protected void m() {
				}
			}
		"""
		when:
		def smells = run(code, code2)
		then:
		smells.size() == 1
	}
}
