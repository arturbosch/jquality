package com.gitlab.artismarti.smartsmells.common

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration

/**
 * @author artur
 */
class SignatureHelper {

	static String createSignature(ClassOrInterfaceDeclaration n) {
		def types = n.typeParameters.join(",")
		def extend = n.extends.join(",")
		def implement = n.implements.join(",")
		return "$n.name${if (types) "<$types>" else ""}${if (extend) " extends $extend" else ""}${if (implement) " implements $implement" else ""}"
	}

}
