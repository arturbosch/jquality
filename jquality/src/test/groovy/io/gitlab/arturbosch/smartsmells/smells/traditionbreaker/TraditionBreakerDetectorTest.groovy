package io.gitlab.arturbosch.smartsmells.smells.traditionbreaker

import io.gitlab.arturbosch.smartsmells.DetectorSpecification
import io.gitlab.arturbosch.smartsmells.api.Detector
import io.gitlab.arturbosch.smartsmells.common.Test

/**
 * @author Artur Bosch
 */
class TraditionBreakerDetectorTest extends DetectorSpecification<TraditionBreaker> {

	@Override
	Detector<TraditionBreaker> detector() {
		return new TraditionBreakerDetector()
	}

	def "one"() {
		given: "detector with manipulated thresholds"
		def path = Test.BASE_PATH.resolve("bla")
		def resolver = resolve(path)
		def info = resolver.find(path.resolve("SelfEnvy.java")).get()
		def detector = new TraditionBreakerDetector(new TBConfig(0, 0.00d, 0, 0, 0))

		when: "running on god class dummy"
		def smells = detector.execute(info, resolver)

		then: "it will find one"
		smells.size() == 1
	}
}
