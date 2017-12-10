package io.gitlab.arturbosch.smartsmells.smells.brainmethod

import io.gitlab.arturbosch.smartsmells.DetectorSpecification
import io.gitlab.arturbosch.smartsmells.api.Detector

/**
 * @author Artur Bosch
 */
class BrainMethodDetectorTest extends DetectorSpecification<BrainMethod> {

	@Override
	Detector<BrainMethod> detector() {
		return new BrainMethodDetector()
	}

	def "find one"() {
		expect:
		smells.size() == 1
		where:
		smells = run(BMDummy.code)
	}
}

class BMDummy {
	static String code = """
			class A {
				void brainMethod(String oO) {
					int i0 = 1;
					int i1 = 1;
					int i2 = 1;
					int i3 = 1;
					int i4 = 1;
					int i4 = 1;
					int i5 = 1;
					int i6 = 1;
					if(true){
						if(true){
							if(true) {} // nesting & cyclo
						}
					}	
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
				}
			}
		"""
}
