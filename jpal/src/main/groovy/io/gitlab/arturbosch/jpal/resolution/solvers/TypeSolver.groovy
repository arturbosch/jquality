package io.gitlab.arturbosch.jpal.resolution.solvers

import com.github.javaparser.ast.type.PrimitiveType
import com.github.javaparser.ast.type.Type
import com.github.javaparser.ast.type.VoidType
import groovy.transform.CompileStatic
import io.gitlab.arturbosch.jpal.ast.TypeHelper
import io.gitlab.arturbosch.jpal.core.CompilationStorage
import io.gitlab.arturbosch.jpal.internal.JdkHelper
import io.gitlab.arturbosch.jpal.internal.Validate
import io.gitlab.arturbosch.jpal.resolution.QualifiedType
import io.gitlab.arturbosch.jpal.resolution.ResolutionData

/**
 * Provides a static method to resolve the full qualified name of a class type.
 * Information about the imports, the package and assumptions of jdk classes are used
 * to predict the qualified type. Be aware that this approach does not work 100% as
 * star imports are ignored - they are generally considered as a code smell.
 *
 * {@code
 *
 * Usage:
 *
 * 	ClassOrInterfaceType myType = ...
 * 	CompilationUnit unit = ...
 * 	ResolutionData data = ResolutionData.of(unit)
 * 	QualifiedType qualifiedType = TypeSolver.getQualifiedTypeFromPackage(data, myType)
 *
 *}
 *
 * @author artur
 */
@CompileStatic
final class TypeSolver {

	private CompilationStorage storage

	TypeSolver(CompilationStorage storage = null) {
		this.storage = storage
	}

	/**
	 * Tries to find the correct qualified name. Considered options are
	 * primitives, boxed primitives, jdk types and reference types within imports or the package.
	 * This approach works on class or interface types as this type is searched
	 * from within the given type.
	 *
	 * @param data resolution data which must be provided from a compilation unit
	 * @param type type of given declaration
	 * @return the qualified type of given type
	 */
	QualifiedType getQualifiedType(ResolutionData data, Type type) {
		Validate.notNull(data)
		Validate.notNull(type)

		if (type instanceof VoidType) {
			return QualifiedType.VOID
		}

		if (type instanceof PrimitiveType) {
			return new QualifiedType((type as PrimitiveType).type.name(), QualifiedType.TypeToken.PRIMITIVE)
		}

		def maybeType = TypeHelper.getClassOrInterfaceType(type).orElse(null)
		if (maybeType) {
			def realType = data.appendOuterTypeIfInnerType(maybeType)
			if (realType.isBoxedType()) {
				return new QualifiedType("java.lang." + realType.name, QualifiedType.TypeToken.BOXED_PRIMITIVE)
			} else {
				String typeName = realType.name
				def maybeFromImports = getFromImports(typeName, data)
				if (maybeFromImports.isPresent()) {
					return maybeFromImports.get()
				} else {
					if (JdkHelper.isPartOfJava(typeName)) {
						return new QualifiedType("java.lang." + typeName, QualifiedType.TypeToken.JAVA_REFERENCE)
					}
					// lets assume it is in the same package
					return new QualifiedType("$data.packageName.$typeName", QualifiedType.TypeToken.REFERENCE)
				}
			}
		}

		return QualifiedType.UNKNOWN
	}

	private Optional<QualifiedType> getFromImports(String importName, ResolutionData data) {
		Validate.notEmpty(importName)

		def imports = data.imports
		if (imports.keySet().contains(importName)) {
			def qualifiedName = imports.get(importName)
			def qualifiedNameWithInnerClass = qualifiedName.substring(0, qualifiedName.lastIndexOf('.') + 1) + importName
			def typeToken = qualifiedName.startsWith("java") ?
					QualifiedType.TypeToken.JAVA_REFERENCE : QualifiedType.TypeToken.REFERENCE
			return Optional.of(new QualifiedType(qualifiedNameWithInnerClass, typeToken))
		} else if (storage) {
			return data.importsWithAsterisk.stream()
					.map { new QualifiedType("$it.$importName", QualifiedType.TypeToken.REFERENCE) }
					.filter { storage.getCompilationInfo(it).isPresent() }
					.findFirst()

		}
		Optional.empty()
	}
}
