package io.gitlab.arturbosch.jpal.core

import io.gitlab.arturbosch.jpal.resolution.Resolver

/**
 * @author Artur Bosch
 */
interface CompilationInfoProcessor {

	void setup(CompilationInfo info, Resolver resolver)

	void process(CompilationInfo info, Resolver resolver)

	void cleanup(CompilationInfo info, Resolver resolver)

}
