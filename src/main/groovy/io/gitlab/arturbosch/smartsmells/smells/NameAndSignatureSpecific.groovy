package io.gitlab.arturbosch.smartsmells.smells

import io.gitlab.arturbosch.smartsmells.common.DetectionResult

/**
 * @author Artur Bosch
 */
interface NameAndSignatureSpecific extends DetectionResult {
	String name()
	String signature()
}