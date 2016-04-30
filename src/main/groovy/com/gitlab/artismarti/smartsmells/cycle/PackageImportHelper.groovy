package com.gitlab.artismarti.smartsmells.cycle

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.type.PrimitiveType
import com.github.javaparser.ast.type.Type
import com.gitlab.artismarti.smartsmells.common.JdkHelper
import com.gitlab.artismarti.smartsmells.common.TypeHelper

/**
 * @author artur
 */
class PackageImportHelper {

	String packageName
	Map<String, String> imports

	PackageImportHelper(PackageDeclaration packageDeclaration, List<ImportDeclaration> imports) {
		this.packageName = Optional.ofNullable(packageDeclaration).map {it.packageName}.orElse("")
		this.imports = imports.collectEntries {
			[Arrays.asList(it.toStringWithoutComments().split("\\.")).last(), it.name.toStringWithoutComments()]
		}
	}

	QualifiedType getQualifiedType(Type type) {

		if (type instanceof PrimitiveType) {
			return new QualifiedType(type.type.toString(), QualifiedType.TypeToken.PRIMITIVE)
		}

		def maybeClassOrInterfaceType = TypeHelper.getClassOrInterfaceType(type)

		if (maybeClassOrInterfaceType.isPresent()) {
			def realType = maybeClassOrInterfaceType.get()
			if (realType.isBoxedType()) {
				return new QualifiedType("java.lang." + realType.name, QualifiedType.TypeToken.BOXED_PRIMITIVE)
			} else {
				String name = realType.name
				if (imports.entrySet().contains(name)) {
					String qualifiedName = imports.get(name)
					return new QualifiedType(qualifiedName, QualifiedType.TypeToken.REFERENCE)
				} else {
					if (JdkHelper.isPartOfJavaLang(name)) {
						return new QualifiedType("java.lang." + name, QualifiedType.TypeToken.JAVA_REFERENCE)
					}
					// lets assume it is in the same package
					return new QualifiedType("$packageName.$name", QualifiedType.TypeToken.REFERENCE)
				}
			}
		}

		return new QualifiedType("UNKNOWN", QualifiedType.TypeToken.UNKNOWN)
	}
}
