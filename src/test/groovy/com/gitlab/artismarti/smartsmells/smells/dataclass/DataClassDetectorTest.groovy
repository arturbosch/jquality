package com.gitlab.artismarti.smartsmells.smells.dataclass

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class DataClassDetectorTest extends Specification {

	def "find one data class with only getters"() {
		expect:
		smells.size() == 1
		smells.getAt(0).name == "DataClassDummy"
		smells.getAt(0).signature == "DataClassDummy"
		smells.getAt(0).sourcePath.path.endsWith("DataClassDummy.java")

		where:
		smells = new DataClassDetector().run(Test.DATA_CLASS_DUMMY_PATH)
	}

	def "find one data class with empty body"() {
		expect:
		smells.size() == 1
		smells.getAt(0).name == "EmptyDataClassDummy"
		smells.getAt(0).signature == "EmptyDataClassDummy<T> extends Object"

		where:
		smells = new DataClassDetector().run(Test.EMPTY_DATA_CLASS_DUMMY_PATH)
	}
}
