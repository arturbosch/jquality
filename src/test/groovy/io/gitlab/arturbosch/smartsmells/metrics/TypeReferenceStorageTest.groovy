package io.gitlab.arturbosch.smartsmells.metrics

import io.gitlab.arturbosch.jpal.core.JPAL
import io.gitlab.arturbosch.jpal.resolution.Resolver
import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class TypeReferenceStorageTest extends Specification {

	@Ignore
	def "stuff"() {
		given:
		def storage = JPAL.new(Test.BASE_PATH)
		when:
		def referenceStorage = new TypeReferenceStorage(new Resolver(storage))
		referenceStorage.create(storage.allCompilationInfo)
		referenceStorage.cache.each { println it }
		then:
		!referenceStorage.cache.isEmpty()
	}
}
