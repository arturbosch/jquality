package com.gitlab.artismarti.smartsmells.dataclass

import com.gitlab.artismarti.smartsmells.common.Test
import spock.lang.Specification

/**
 * @author artur
 */
class DataClassDetectorTest extends Specification {

	def "find one data class"() {
		expect:
		smells.size() == 2
		smells.get(0).name == "DataClassDummy"
		smells.get(0).signature == "DataClassDummy"
		smells.get(0).sourcePath.path == "/home/artur/Repos/SmartSmells/src/test/groovy/com/gitlab/artismarti/smartsmells/java/DataClassDummy.java"
		smells.get(0).sourceRange.toString() == "[[6, 1], [35, 1]]"
		smells.get(1).name == "EmptyDataClassDummy"
		smells.get(1).signature == "EmptyDataClassDummy<T> extends Object"

		where:
		smells = new DataClassDetector().run(Test.PATH)
	}
}
