package com.gitlab.artismarti.smartsmells.common

import groovy.transform.ToString

/**
 * @author artur
 */
@ToString(includeNames = false, includePackage = false)
class QualifiedType {

	String name
	TypeToken typeToken

	enum TypeToken {
		PRIMITIVE, BOXED_PRIMITIVE, REFERENCE, JAVA_REFERENCE, UNKNOWN
	}

	QualifiedType(String name, TypeToken typeToken) {
		this.name = name
		this.typeToken = typeToken
	}

	boolean isPrimitive() {
		return typeToken == TypeToken.PRIMITIVE || typeToken == TypeToken.BOXED_PRIMITIVE
	}

	boolean isFromJdk() {
		return typeToken == TypeToken.JAVA_REFERENCE
	}

	boolean isReference() {
		return typeToken == TypeToken.REFERENCE
	}

	String shortName() {
		def index = name.lastIndexOf(".")
		if (index == -1)
			return name
		return name.substring(index + 1)
	}

	String asStringPathToJavaFile() {
		def tmp = name
		if (isInnerClass()) {
			def lastIndexOf = name.lastIndexOf(".")
			tmp = name.substring(0, lastIndexOf)
		}
		return "${tmp.replaceAll("\\.", "/")}.java"
	}

	boolean isInnerClass() {
		def tokens = Arrays.asList(name.split("\\."))
		if (tokens.size() > 1) {
			def secondLastToken = tokens.get(tokens.size() - 2)
			return !secondLastToken.isEmpty() && Character.isUpperCase(secondLastToken.charAt(0))
		}
		return false
	}
}
