package io.gitlab.arturbosch.smartsmells.smells.classdatashouldbeprivate

import io.gitlab.arturbosch.smartsmells.DetectorSpecification

/**
 * @author Artur Bosch
 */
class ClassDataShouldBePrivateDetectorTest extends DetectorSpecification<ClassDataShouldBePrivate> {

	@Override
	ClassDataShouldBePrivateDetector detector() {
		return new ClassDataShouldBePrivateDetector()
	}

	def "should detect class with public fields"() {
		given:
		def code = """
			class A {
				public String name;
				public int i;
			}
		"""

		when:
		def smells = run(code)

		then:
		smells.size() == 1
		smells[0].signature == "A"
	}

	def "should not detect static class with public fields"() {
		given:
		def code = """
			class A {
				static class B {
					public String name;
					public int i;
				}
			}
		"""

		when:
		def smells = run(code)

		then:
		smells.isEmpty()
	}

	def "should not detect class without public fields"() {
		given:
		def code = """
			class A {
				String name;
				int i;
			}
		"""

		when:
		def smells = run(code)

		then:
		smells.isEmpty()
	}

	def "should not detect class with public static fields"() {
		given:
		def code = """
			class A {
				public static String name = "";
				public final static int i = 5;
			}
		"""

		when:
		def smells = run(code)

		then:
		smells.isEmpty()
	}

	def "should detect class with public fields in nested depth 3"() {
		given:
		def code = """
			class A {
				static class B {
					public String name;
					public int i;
					class C {
						class D {
							public String name;
							public int i;
						}
					}
				}
			}
		"""

		when:
		def smells = run(code)

		then:
		smells.size() == 1
		smells[0].signature == "A\$B\$C\$D"
	}
}
