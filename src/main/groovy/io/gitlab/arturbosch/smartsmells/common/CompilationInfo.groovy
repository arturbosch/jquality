package io.gitlab.arturbosch.smartsmells.common

import com.github.javaparser.ASTHelper
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.type.ClassOrInterfaceType
import io.gitlab.arturbosch.smartsmells.common.helper.TypeHelper

import java.nio.file.Path

/**
 * @author artur
 */
class CompilationInfo {

	QualifiedType qualifiedType
	CompilationUnit unit
	Path path
	List<QualifiedType> usedTypes
	Set<QualifiedType> innerClasses

	private CompilationInfo(QualifiedType qualifiedType, CompilationUnit unit, Path path,
	                        List<QualifiedType> usedTypes, Set<QualifiedType> innerClasses) {

		this.qualifiedType = qualifiedType
		this.unit = unit
		this.path = path
		this.usedTypes = usedTypes
		this.innerClasses = innerClasses
	}

	static CompilationInfo of(QualifiedType qualifiedType, CompilationUnit unit, Path path) {
		List<QualifiedType> types = unit.imports.stream()
				.filter { !it.isEmptyImportDeclaration() }
				.map { it.name.toStringWithoutComments() }
				.filter { !it.startsWith("java") }
				.map { new QualifiedType(it, QualifiedType.TypeToken.REFERENCE) }
				.collect()
		def innerClasses = TypeHelper.getQualifiedTypesOfInnerClasses(unit)
		return new CompilationInfo(qualifiedType, unit, path, types, innerClasses)
	}

	boolean isWithinScope(QualifiedType type) {
		return usedTypes.contains(type) || searchForTypeWithinUnit(this, type)
	}

	private static boolean searchForTypeWithinUnit(CompilationInfo info, QualifiedType qualifiedType) {
		def name = qualifiedType.asOuterClass().name
		def packageName = name.substring(0, name.lastIndexOf("."))

		def samePackage = Optional.ofNullable(info.unit.package).map { it.packageName == packageName }

		if (samePackage.isPresent()) {
			return searchInternal(qualifiedType, info)
		} else {
			def sameImport = info.usedTypes.stream()
					.filter { it.name.contains('*') }
					.map { it.name.substring(0, it.name.lastIndexOf('.')) }
					.filter { it == packageName }
					.findFirst()
			if (sameImport.isPresent()) {
				return searchInternal(qualifiedType, info)
			}
		}
		return false
	}

	private static boolean searchInternal(QualifiedType qualifiedType, CompilationInfo info) {
		def shortName = qualifiedType.shortName()
		def types = ASTHelper.getNodesByType(info.unit, ClassOrInterfaceType.class)
		return types.any { it.name == shortName }
	}

	@Override
	public String toString() {
		return "CompilationInfo{" +
				"qualifiedType=" + qualifiedType +
				'}';
	}
}
