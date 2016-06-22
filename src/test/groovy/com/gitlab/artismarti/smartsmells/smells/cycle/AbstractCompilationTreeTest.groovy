package com.gitlab.artismarti.smartsmells.smells.cycle

import com.gitlab.artismarti.smartsmells.common.CompilationTree
import spock.lang.Specification

/**
 * @author artur
 */
abstract class AbstractCompilationTreeTest extends Specification {

	def cleanup() {
		CompilationTree.reset()
	}

}
