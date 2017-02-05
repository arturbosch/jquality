package io.gitlab.arturbosch.smartsmells.smells
/**
 * @author Artur Bosch
 */
interface NameAndSignatureSpecific extends DetectionResult {
	String name()
	String signature()
}