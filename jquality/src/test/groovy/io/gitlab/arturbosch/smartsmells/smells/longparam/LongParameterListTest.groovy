package io.gitlab.arturbosch.smartsmells.smells.longparam

import io.gitlab.arturbosch.jpal.ast.source.SourcePath
import io.gitlab.arturbosch.jpal.ast.source.SourceRange
import io.gitlab.arturbosch.smartsmells.common.Test
import io.gitlab.arturbosch.smartsmells.smells.ElementTarget
import spock.lang.Specification

/**
 * @author artur
 */
class LongParameterListTest extends Specification {

	def "long param list can use fields of long method through delegation"() {
		expect:
		lpl.name == "name"
		lpl.sourcePath == SourcePath.of(Test.PATH, Test.PATH)
		Test.PATH.toAbsolutePath().toString().contains(lpl.path)

		where:
		lpl = new LongParameterList("name", "signature", 1, 1, ["1", "2"],
				SourceRange.of(1, 1, 1, 1), SourcePath.of(Test.PATH, Test.PATH), ElementTarget.METHOD)
	}
}
