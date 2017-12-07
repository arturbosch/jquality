package io.gitlab.arturbosch.smartsmells.smells.RefusedParentBequest

import io.gitlab.arturbosch.smartsmells.DetectorSpecification
import io.gitlab.arturbosch.smartsmells.api.Detector

/**
 * @author Artur Bosch
 */
class RefusedParentBequestSameFileTest extends DetectorSpecification<RefusedParentBequest> {

	@Override
	Detector<RefusedParentBequest> detector() {
		return new RefusedParentBequestDetector()
	}

	def "find none inside same file"() {
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
								super.m();
							}
						}
					}
				}
			}
			class B {
				protected void m() {
				}
			}
		"""
		when:
		def smells = run(code)
		then:
		smells.isEmpty()
	}
}
