package com.gitlab.artismarti.smartsmells.common

import groovy.transform.ToString

/**
 * @author artur
 */
@ToString(includeNames = false, includePackage = false)
class QualifiedType {

	String name

	String asStringPathToJavaFile() {
		def tmp = name
		if (isInnerClass()) {
			println "Inner class: " + name
			def lastIndexOf = name.lastIndexOf(".")
			tmp = name.substring(0, lastIndexOf)
			println "LastIndex: $tmp"
		}
		"${tmp.replaceAll("\\.", "/")}.java"
	}

	boolean isInnerClass() {
		def split = Arrays.asList(name.split("\\."))
		if (split.size() > 1) {
			def get = split.get(split.size() - 2)
			println get
			return Character.isUpperCase(get.charAt(0))
		}
		return false
	}

	enum TypeToken {
		PRIMITIVE, BOXED_PRIMITIVE, REFERENCE, JAVA_REFERENCE, UNKNOWN
	}

	TypeToken typeToken

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
}
