package io.gitlab.arturbosch.smartsmells.smells.cycle

import io.gitlab.arturbosch.jpal.core.CompilationTree
import spock.lang.Specification

/**
 * @author artur
 */
abstract class AbstractCompilationTreeTest extends Specification {

	def cleanup() {
		CompilationTree.reset()
	}

}
