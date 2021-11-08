package io.gitlab.arturbosch.jpal.ast.source

import io.gitlab.arturbosch.jpal.Helper
import spock.lang.Specification

/**
 * @author Artur Bosch
 */
class SourceRangeTest extends Specification {

	def "test from node"() {
		expect:
		sourceRange.startLine == 1
		sourceRange.endLine == 7
		sourceRange.startColumn == 1
		sourceRange.endColumn == 2
		where:
		sourceRange = SourceRange.fromNode(Helper.compile(Helper.EMPTY_DUMMY))
	}
}
