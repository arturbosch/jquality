package io.gitlab.arturbosch.jpal.ast

import com.github.javaparser.ast.body.EnumDeclaration
import io.gitlab.arturbosch.jpal.internal.Validate

/**
 * @author Artur Bosch
 */
final class EnumHelper {

	private EnumHelper() {
	}

	/**
	 * Creates a signature of given enum inclusive implemented types.
	 *
	 * @param n given enum type
	 * @return the signature as a string
	 */
	static String createSignature(EnumDeclaration n) {
		Validate.notNull(n)
		def implement = n.implementedTypes.join(", ")
		return "$n.name${if (implement) " implements $implement" else ""}"
	}

	/**
	 * Creates a full signature of given class with respect to anonymous or inner class checks.
	 *
	 * @param n given class type
	 * @return the full unique signature as a string
	 */
	static String createFullSignature(EnumDeclaration n) {
		Validate.notNull(n)
		String signature = ""
		NodeHelper.findDeclaringType(n)
				.ifPresent { signature = ClassHelper.createFullSignature(it) + "\$$signature" }
		NodeHelper.findDeclaringMethod(n)
				.ifPresent { signature = signature + "${it.name}\$" }
		return signature + createSignature(n)
	}

}
