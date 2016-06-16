package com.gitlab.artismarti.smartsmells.common.helper

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.gitlab.artismarti.smartsmells.common.source.SourceRange

/**
 * @author artur
 */
class BadSmellHelper {

	static String createClassSignature(ClassOrInterfaceDeclaration n) {
		String signature = ""
		NodeHelper.findDeclaringClass(n)
				.ifPresent { signature = createSignature(it) + "\$$signature" }
		NodeHelper.findDeclaringMethod(n)
				.ifPresent { signature = signature + "${it.name}\$" }
		return signature + createSignature(n)
	}

	static String createSignature(ClassOrInterfaceDeclaration n) {
		def types = n.typeParameters.join(",")
		def extend = n.extends.join(",")
		def implement = n.implements.join(",")
		return "$n.name${if (types) "<$types>" else ""}${if (extend) " extends $extend" else ""}${if (implement) " implements $implement" else ""}"
	}

	static SourceRange createSourceRangeFromNode(Node it) {
		SourceRange.of(it.getBeginLine(), it.getEndLine(), it.getBeginColumn(), it.getEndColumn())
	}

}
