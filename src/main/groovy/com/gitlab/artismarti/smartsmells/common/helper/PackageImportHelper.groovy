package com.gitlab.artismarti.smartsmells.common.helper

import com.github.javaparser.ast.type.PrimitiveType
import com.github.javaparser.ast.type.Type
import com.gitlab.artismarti.smartsmells.common.QualifiedType

/**
 * @author artur
 */
class PackageImportHelper {

	static QualifiedType getQualifiedType(PackageImportHolder holder, Type type) {
		if (type instanceof PrimitiveType) {
			return new QualifiedType(type.type.toString(), QualifiedType.TypeToken.PRIMITIVE)
		}

		def maybeClassOrInterfaceType = TypeHelper.getClassOrInterfaceType(type)

		if (maybeClassOrInterfaceType.isPresent()) {
			def realType = maybeClassOrInterfaceType.get()
			if (realType.isBoxedType()) {
				return new QualifiedType("java.lang." + realType.name, QualifiedType.TypeToken.BOXED_PRIMITIVE)
			} else {
				String name = realType.toString()
				def imports = holder.imports
				if (imports.entrySet().contains(name)) {
					String qualifiedName = imports.get(name)
					return new QualifiedType(qualifiedName, QualifiedType.TypeToken.REFERENCE)
				} else {
					if (JdkHelper.isPartOfJavaLang(name)) {
						return new QualifiedType("java.lang." + name, QualifiedType.TypeToken.JAVA_REFERENCE)
					}
					// lets assume it is in the same package
					return new QualifiedType("$holder.packageName.$name", QualifiedType.TypeToken.REFERENCE)
				}
			}
		}

		return new QualifiedType("UNKNOWN", QualifiedType.TypeToken.UNKNOWN)
	}

}
