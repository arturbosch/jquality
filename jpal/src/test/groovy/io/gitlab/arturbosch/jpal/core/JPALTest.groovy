package io.gitlab.arturbosch.jpal.core

import io.gitlab.arturbosch.jpal.Helper
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class JPALTest extends Specification {

	def "updatable storage has no start info's"() {
		expect:
		storage.getAllCompilationInfo().isEmpty()
		where:
		storage = JPAL.updatable()
	}

	def "configure updatable storage through builder"() {
		given:
		def builder = JPAL.builder()
		when:
		def storage = builder.updatable()
				.withFilters(null)
				.withProcessor(null)
				.withRoot(null)
				.build()
		then:
		storage instanceof UpdatableCompilationStorage
	}

	def "configure storage with root"() {
		when:
		def storage = JPAL.builder().withRoot(Helper.DUMMY).build()
		then:
		storage instanceof CompilationStorage
		storage.allCompilationInfo.size() == 1
	}

	def "invalid builder configuration result in illegal state exception"() {
		when:
		JPAL.builder().build()
		then:
		thrown(IllegalStateException.class)
	}

}
