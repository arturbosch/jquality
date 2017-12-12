package io.gitlab.arturbosch.smartsmells.smells

import groovy.transform.CompileStatic

/**
 * @author Artur Bosch
 */
@CompileStatic
interface NameAndSignatureSpecific extends DetectionResult {
	String name()

	String signature()
}
