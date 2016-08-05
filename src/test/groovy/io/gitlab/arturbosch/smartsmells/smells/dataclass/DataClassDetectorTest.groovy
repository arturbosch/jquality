package io.gitlab.arturbosch.smartsmells.smells.dataclass

import io.gitlab.arturbosch.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class DataClassDetectorTest extends Specification {

	def "find one data class with only getters"() {
		expect:
		smells.size() == 1
		smells[0].name == "DataClassDummy"
		smells[0].signature == "DataClassDummy"
		smells[0].sourcePath.path.endsWith("DataClassDummy.java")

		where:
		smells = new DataClassDetector().run(Test.DATA_CLASS_DUMMY_PATH)
	}

	def "find one data class with empty body"() {
		expect:
		smells.size() == 1
		smells[0].name == "EmptyDataClassDummy"
		smells[0].signature == "EmptyDataClassDummy<T> extends Object"

		where:
		smells = new DataClassDetector().run(Test.EMPTY_DATA_CLASS_DUMMY_PATH)
	}
}
